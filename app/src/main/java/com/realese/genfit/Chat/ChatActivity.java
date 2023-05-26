package com.realese.genfit.Chat;



import static com.theokanning.openai.service.OpenAiService.defaultClient;
import static com.theokanning.openai.service.OpenAiService.defaultObjectMapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.card.MaterialCardView;
import com.realese.genfit.Frags.FragMain;
import com.realese.genfit.R;
import com.realese.genfit.retrofit.Request;
import com.realese.genfit.retrofit.Response;
import com.realese.genfit.retrofit.RetrofitService;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
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
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
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
                    send(txtToPrompt(Arrays.asList("여자", "170cm", "70kg"), message));
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

    public String tagToString(List<String> tags){
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i != tags.size() -1){
                b.append(tags.get(i)).append(", ");
            }else{
                b.append(tags.get(i));
            }
        }
        return b.toString();
    }

    public String txtToPrompt(List<String> tags, String text){
        String tagStr = tagToString(tags);

        return "지금부터 넌 가장 좋은 옷 한 세트만 추천해주는 공손한 패션 코디네이터야.\n" +
                "키워드인 \"" + tagStr +"\"와 \"" + text +"\" 라는 텍스트를 참고하여 상의, 하의, 신발, 액세서리 등을 1개씩 디테일하게 색깔과 이름이 포함된 키워드 형태로 추천해줘." +
                "그리고 마지막에는 그 옷을 왜 골랐는지 존댓말로 설명해 줘." +
                "\n" +
                "예를 들어,\n" +
                "\n" +
                "상의 : 색깔 상의 이름\n" +
                "하의 : 색깔 하의 이름\n" +
                "신발 : 색깔 신발 이름\n" +
                "액세서리 : 색깔 액세서리 이름\n" +
                "\n" +
                "로 추천해줄 수 있어.";
    }

    public void send(String text) {

        Chat userChat = new Chat(text, false);
        chats.add(userChat);


        Chat gptChat = new Chat("", true);
        gptChat.state = Chat.STATE_LOAD_TEXT;


        chats.add(gptChat);

        adopter.notifyDataSetChanged();
        rc.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                rc.smoothScrollToPosition(adopter.getItemCount() - 1);
            }
        });


        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(getString(R.string.API_KEY), Duration.ofSeconds(500))
                .newBuilder()
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();

        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api, client.dispatcher().executorService());

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), text);
        messages.add(chatMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(messages)
                .stream(true)
                .n(1)
                .maxTokens(2048)
                .model("gpt-3.5-turbo")
                .logitBias(new HashMap<>())
                .build();

        service.streamChatCompletion(request).subscribeOn(Schedulers.io()).subscribe(chunk -> {
            ChatCompletionChoice choice = chunk.getChoices().get(0);
            if (choice.getFinishReason() != null) {
                gptChat.gptCallback.onComplete();
                return;
            }
            ChatMessage message = choice.getMessage();
            if (message.getContent() != null) {
                gptChat.gptCallback.onReceived(message);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                gptChat.gptCallback.onFail();
            }
        });


        gptChat.gptCallback = new GptCallback() {
            @Override
            void onReceived(ChatMessage message) {
                if (gptChat.holder == null) return;
                gptChat.text += message.getContent();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (gptChat.holder == null) return;
                        ChatListAdopter.ViewHolder holder = gptChat.holder;
                        holder.tv_op.setText(gptChat.text);
                        holder.tv_op.setVisibility(View.VISIBLE);
                        holder.tv_load.setVisibility(View.GONE);
                        holder.loadView.setVisibility(View.GONE);
                        holder.loadView.pauseAnimation();
                        rc.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                rc.smoothScrollToPosition(adopter.getItemCount() - 1);
                            }
                        });
                    }
                });
            }

            @Override
            void onComplete() {
                gptChat.state = Chat.STATE_COMPLETE_TEXT;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        service.shutdownExecutor();
                        if (gptChat.holder == null) return;
                        gptChat.holder.change.setTag(gptChat);
                        gptChat.holder.change.setVisibility(View.VISIBLE);
                        gptChat.holder.change.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setVisibility(View.GONE);
                                Chat c = (Chat) v.getTag();
                                outFitToPrompt(c);
                            }
                        });
                        rc.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                rc.smoothScrollToPosition(adopter.getItemCount() - 1);
                            }
                        });
                    }
                });
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

    public void outFitToPrompt(Chat chat){

        if (chat.holder != null) {
            chat.holder.loadView.setVisibility(View.VISIBLE);
            chat.holder.tv_load.setVisibility(View.VISIBLE);
            chat.holder.loadView.playAnimation();
            chat.holder.tv_load.setText("이미지 생성 중...");
            chat.state = Chat.STATE_LOAD_IMAGE;
        }




        String p = "Translate the resulting outfit into an \"English command\" consisting of words and commas. " +
                "For example, you could structure your command as \"keyword1, keyword2, keyword3\".";

        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(getString(R.string.API_KEY), Duration.ofSeconds(500))
                .newBuilder()
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();

        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api, client.dispatcher().executorService());

        final List<ChatMessage> messages = new ArrayList<>();

        final ChatMessage chatMessage1 = new ChatMessage(ChatMessageRole.ASSISTANT.value(), chat.text);
        final ChatMessage chatMessage2 = new ChatMessage(ChatMessageRole.USER.value(), p);

        messages.add(chatMessage1);
        messages.add(chatMessage2);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(messages)
                .stream(false)
                .n(1)
                .maxTokens(2048)
                .model("gpt-3.5-turbo")
                .logitBias(new HashMap<>())
                .build();

        GptCallback gptCallback = new GptCallback() {
            @Override
            void onReceived(ChatMessage message) {
                chat.prompt += message.getContent();
            }

            @Override
            void onComplete() {
                promptToImage(chat);
            }

            @Override
            void onFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        chat.holder.tv_load.setVisibility(View.GONE);
                        chat.holder.loadView.setVisibility(View.GONE);
                        chat.holder.loadView.pauseAnimation();

                        chat.holder.change.setVisibility(View.VISIBLE);
                    }
                });

            }

        };


        service.streamChatCompletion(request).subscribeOn(Schedulers.io()).subscribe(chunk -> {
            ChatCompletionChoice choice = chunk.getChoices().get(0);
            if (choice.getFinishReason() != null) {
                gptCallback.onComplete();
                return;
            }
            ChatMessage message = choice.getMessage();
            if (message.getContent() != null) {
                gptCallback.onReceived(message);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                gptCallback.onFail();
            }
        });



    }

    public static byte[] StringToBitmap(String encodedString) {
        try {
            return Base64.decode(encodedString, Base64.DEFAULT);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Request request = chain.request();
            okhttp3.Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    public File saveCache(byte[] bytes){
        File file = new File(getFilesDir(), System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }



    public void promptToImage(Chat chat){

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor("kim1914", "q23kkr"))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://173.49.207.77:42006/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //JSON 요청 본문 참조 바람. (https://cloud.google.com/vision/docs/ocr?hl=ko#detect_text_in_a_local_image)

        Request request = new Request();
        request.setPrompt(chat.prompt);

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);


        Call<Response> call = retrofitService.getPosts(request);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                chat.state = Chat.STATE_COMPLETE_IMAGE;
                if (response.isSuccessful() && response.body() != null){
                    String base = response.body().images.get(0).split(",", 1)[0];
                    byte[] bytes = StringToBitmap(base);
                    chat.imagePath = saveCache(bytes);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (chat.holder != null) {

                                chat.holder.cdiv.setVisibility(View.VISIBLE);
                                chat.holder.imageView.setVisibility(View.VISIBLE);
                                chat.holder.imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        chat.holder.imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        rc.smoothScrollToPosition(adopter.getItemCount()-1);
                                    }
                                });
                                Glide.with(ChatActivity.this).load(chat.imagePath).addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                }).into(chat.holder.imageView);
                            }
                        }
                    });
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chat.holder != null) {
                            chat.holder.tv_load.setVisibility(View.GONE);
                            chat.holder.loadView.setVisibility(View.GONE);
                            chat.holder.loadView.pauseAnimation();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("stable", t.toString());

                chat.state = Chat.STATE_COMPLETE_IMAGE;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chat.holder != null) {
                            chat.holder.tv_load.setVisibility(View.GONE);
                            chat.holder.loadView.setVisibility(View.GONE);
                            chat.holder.loadView.pauseAnimation();
                        }
                    }
                });
            }

        });
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
        String prompt = "";
        File imagePath;
        GptCallback gptCallback;
        ChatListAdopter.ViewHolder holder;


        Chat(String text, boolean isGPT){
            this.text = text;
            this.isGPT = isGPT;
        }

    }

    abstract class GptCallback{

        void onReceived(ChatMessage message){}

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
                  holder.cdiv.setVisibility(View.GONE);

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

                  }else if (chat.state == Chat.STATE_COMPLETE_TEXT){

                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.tv_load.setVisibility(View.GONE);
                      holder.loadView.setVisibility(View.GONE);
                      holder.loadView.pauseAnimation();
                      holder.tv_op.setText(chat.text);

                      holder.change.setVisibility(View.VISIBLE);

                      holder.change.setTag(chat);
                      holder.change.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              v.setVisibility(View.GONE);
                              Chat c = (Chat) v.getTag();
                              outFitToPrompt(c);
                          }
                      });
                  }else if (chat.state == Chat.STATE_COMPLETE_IMAGE){

                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.tv_load.setVisibility(View.GONE);
                      holder.loadView.setVisibility(View.GONE);
                      holder.loadView.pauseAnimation();
                      holder.tv_op.setText(chat.text);

                      holder.change.setVisibility(View.GONE);
                      holder.change.setTag(chat);
                      holder.cdiv.setVisibility(View.VISIBLE);
                      Glide.with(ChatActivity.this).load(chat.imagePath).addListener(new RequestListener<Drawable>() {
                          @Override
                          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                              return false;
                          }

                          @Override
                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                              rc.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      // Call smooth scroll
                                      rc.smoothScrollToPosition(adopter.getItemCount() - 1);
                                  }
                              });
                              return false;
                          }
                      }).into(holder.imageView);

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
            MaterialCardView change;
            CardView cdiv;

            ViewHolder(View itemView) {
                super(itemView);
                op_parent = itemView.findViewById(R.id.opponent_view);
                my_parent = itemView.findViewById(R.id.my_view);
                tv_op = itemView.findViewById(R.id.tv_opponent);
                tv_my = itemView.findViewById(R.id.tv_my);
                loadView = itemView.findViewById(R.id.load_view);
                tv_load = itemView.findViewById(R.id.tv_load);
                imageView = itemView.findViewById(R.id.iv_opponent);
                change = itemView.findViewById(R.id.change_image);
                cdiv = itemView.findViewById(R.id.cdiv);
            }
        }


    }




}