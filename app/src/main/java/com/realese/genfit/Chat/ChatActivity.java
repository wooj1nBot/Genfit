package com.realese.genfit.Chat;



import static com.theokanning.openai.service.OpenAiService.defaultClient;
import static com.theokanning.openai.service.OpenAiService.defaultObjectMapper;
import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.realese.genfit.Frags.FragMain;
import com.realese.genfit.R;
import com.realese.genfit.retrofit.Message;
import com.realese.genfit.retrofit.Request;
import com.realese.genfit.retrofit.Response;
import com.realese.genfit.retrofit.RetrofitService;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChatActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView rc;
    ChatListAdopter adopter;
    ArrayList<Chat> chats;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back = findViewById(R.id.back);
        rc = findViewById(R.id.rc);
        chats = new ArrayList<>();
        adopter = new ChatListAdopter(chats);
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(adopter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FragMain.class));
                finish();
            }
        });

        EditText editText = findViewById(R.id.edit);
        ImageView send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                if(!message.equals("")) {
                    send(message, chats);
                    editText.setText("");
                }
            }
        });
        handler = new Handler();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    send.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E2E2E")));
                    send.setClickable(true);

                } else {
                    send.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eeeeee")));
                    send.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




    }

    public void send(String text, ArrayList<Chat> chats) {

        Chat userChat = new Chat(text, false);
        chats.add(userChat);

        Chat gptChat = new Chat("", true);
        gptChat.state = Chat.STATE_LOAD_TEXT;


        chats.add(gptChat);

        adopter.notifyDataSetChanged();
        rc.scrollToPosition(adopter.getItemCount() - 1);


        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(getString(R.string.API_KEY), Duration.ofSeconds(500))
                .newBuilder()
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api, client.dispatcher().executorService());

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), text);
        messages.add(systemMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(messages)
                .stream(true)
                .n(1)
                .model("gpt-3.5-turbo")
                .logitBias(new HashMap<>())
                .build();

        service.streamChatCompletion(request).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(chunk -> {
            String message = chunk.getChoices().get(0).getMessage().getContent();
            if (message == null) {
                gptChat.gptCallback.onComplete();
                return;
            }
            gptChat.gptCallback.onReceived(message);
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                gptChat.gptCallback.onFail();
            }
        });








        gptChat.gptCallback = new GptCallback() {
            @Override
            void onReceived(String message) {
                if (gptChat.holder == null) return;
                gptChat.text += message;
                Log.d("gptchat", gptChat.text);

            }

            @Override
            void onComplete() {
                gptChat.state = Chat.STATE_COMPLETE_TEXT;

                ChatListAdopter.ViewHolder holder = gptChat.holder;
                if (gptChat.holder == null) return;
                holder.tv_op.setVisibility(View.VISIBLE);
                holder.tv_load.setVisibility(View.GONE);
                holder.loadView.setVisibility(View.GONE);
                holder.loadView.pauseAnimation();
            }

            @Override
            void onFail() {
                gptChat.state = Chat.STATE_ERROR;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (gptChat.holder == null) return;
                        ChatListAdopter.ViewHolder holder = gptChat.holder;
                        holder.tv_op.setText("죄송합니다. 오류가 발생했습니다.");
                        holder.tv_op.setVisibility(View.VISIBLE);
                        holder.tv_load.setVisibility(View.GONE);
                        holder.loadView.setVisibility(View.GONE);
                        holder.loadView.pauseAnimation();
                    }
                });

            }

        };


    }





    class Chat{

        public static final int STATE_LOAD_TEXT = 1;
        public static final int STATE_COMPLETE_TEXT = 2;
        public static final int STATE_LOAD_IMAGE = 3;
        public static final int STATE_COMPLETE_IMAGE = 4;
        public static final int STATE_ERROR = 5;

        String text;
        boolean isGPT;
        int state = 1;
        ArrayList<String> tags;
        GptCallback gptCallback;
        ChatListAdopter.ViewHolder holder;


        Chat(String text, boolean isGPT){
            this.text = text;
            this.isGPT = isGPT;
        }

        Chat(String text, boolean isGPT, ArrayList<String> tags){
            this.text = text;
            this.isGPT = isGPT;
        }

    }

    abstract class GptCallback{

        void onReceived(String message){}

        void onComplete(){}

        void onFail(){}
    }





    public class ChatListAdopter extends RecyclerView.Adapter<ChatListAdopter.ViewHolder> {

        ArrayList<Chat> chats;
        Context context;

        ChatListAdopter(ArrayList<Chat> chats){
            this.chats = chats;
        }

        @NonNull
        @Override
        public ChatListAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            View view = inflater.inflate(R.layout.chat_list_item, parent, false);
            return new ChatListAdopter.ViewHolder(view);
        }



        @Override
        public void onBindViewHolder(@NonNull ChatListAdopter.ViewHolder holder, int position) {
              Chat chat = chats.get(position);
              if (chat.isGPT){
                  holder.my_parent.setVisibility(View.GONE);
                  holder.op_parent.setVisibility(View.VISIBLE);

                  chat.holder = holder;

                  if (chat.state == Chat.STATE_LOAD_TEXT || chat.state == Chat.STATE_LOAD_IMAGE){
                      holder.tv_op.setVisibility(View.GONE);
                      holder.loadView.setVisibility(View.VISIBLE);
                      holder.tv_load.setVisibility(View.VISIBLE);
                      holder.loadView.playAnimation();
                      if (chat.state == Chat.STATE_LOAD_IMAGE){
                          holder.tv_load.setText("착샷 생성 중...");
                      }else{
                          holder.tv_load.setText("코디 생성 중...");
                      }
                  } else if (chat.state == Chat.STATE_ERROR) {
                      holder.tv_op.setText("죄송합니다. 오류가 발생했습니다.");
                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.tv_load.setVisibility(View.GONE);
                      holder.loadView.setVisibility(View.GONE);
                      holder.loadView.pauseAnimation();
                  }
                  else{
                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.tv_load.setVisibility(View.GONE);
                      holder.loadView.setVisibility(View.GONE);
                      holder.loadView.pauseAnimation();
                      holder.tv_op.setText(chat.text);
                  }




              }else {
                  holder.my_parent.setVisibility(View.VISIBLE);
                  holder.op_parent.setVisibility(View.GONE);
                  holder.tv_my.setText(chat.text);
              }
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout op_parent;
            RelativeLayout my_parent;

            TextView tv_op;
            TextView tv_my;

            LottieAnimationView loadView;
            TextView tv_load;
            ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                op_parent = itemView.findViewById(R.id.opponent_view);
                my_parent = itemView.findViewById(R.id.my_view);
                tv_op = itemView.findViewById(R.id.tv_opponent);
                tv_my = itemView.findViewById(R.id.tv_my);
                loadView = itemView.findViewById(R.id.load_view);
                tv_load = itemView.findViewById(R.id.tv_load);
                imageView = itemView.findViewById(R.id.iv_opponent);
            }
        }


    }




}