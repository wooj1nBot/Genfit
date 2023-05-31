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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.realese.genfit.BuildConfig;
import com.realese.genfit.Frags.MainActivity;
import com.realese.genfit.GalleryActivity;
import com.realese.genfit.R;
import com.realese.genfit.items.Cody;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;
import com.realese.genfit.retrofit.Request;
import com.realese.genfit.retrofit.Response;
import com.realese.genfit.retrofit.RetrofitService;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    String OPEN_AI_KEY = BuildConfig.OPENAI_API_KEY;


    ImageView back;
    RecyclerView rc;
    ChatListAdopter adopter;
    ArrayList<Chat> chats;
    private Handler handler;

    EditText editText;

    private long pressedTime;

    boolean isInit = true;

    User user;

    List<ChatMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        back = findViewById(R.id.back);
        rc = findViewById(R.id.rc);
        chats = new ArrayList<>();
        messages = new ArrayList<>();

        Chat gptChat = new Chat("", true);
        gptChat.state = Chat.STATE_COMPLETE_TEXT;
        gptChat.isRecommend = false;
        gptChat.text = getString(R.string.help_genny);
        chats.add(gptChat);


        adopter = new ChatListAdopter(chats);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setAdapter(adopter);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText = findViewById(R.id.edit);
        ImageView send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                if(!message.equals("")) {
                    send(message, null, isInit);
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

        if (Util.isLogin(this)){
            Util.getLoginUser(this).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                }
            });
        }else{
            user = Util.getUser(this);
        }


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

        return "지금부터 당신은 가장 좋은 옷 한 세트만 추천해주는 전문 패션 코디네이터입니다.\n" +
                "키워드인 \"" + tagStr +"\"와 \"" + text +"\" 라는 텍스트를 참고하여 상의, 하의, 신발, 액세서리 등을 1개씩 디테일하게 색깔과 이름이 포함된 키워드 형태로 추천해주세요." +
                "그리고 마지막에는 그 옷을 왜 골랐는지 무조건 존댓말로 필요한 말만 설명해 주세요." +
                "\n" +
                "예를 들어,\n" +
                "\n" +
                "상의: 색깔 상의 이름\n" +
                "하의: 색깔 하의 이름\n" +
                "신발: 색깔 신발 이름\n" +
                "액세서리: 색깔 액세서리 이름\n" +
                "\n" +
                "로 추천해줄 수 있습니다.";
    }

    public void send(String text, Chat lastChat, boolean isInit) {
        // gpt한테 보내는 함수




        Chat gptChat;
        if (lastChat == null) {
            Chat userChat = new Chat(text, false);
            chats.add(userChat);

            gptChat = new Chat("", true);
            gptChat.state = Chat.STATE_LOAD_TEXT;
            gptChat.userText = text;

            chats.add(gptChat);

            adopter.notifyDataSetChanged();

            rc.post(new Runnable() {
                @Override
                public void run() {
                    // Call smooth scroll
                    rc.smoothScrollToPosition(getPos(gptChat));
                }
            });

            if (isInit){
                List<String> tags = new ArrayList<>();
                if (user.age != 0){
                    tags.add(user.height + "cm");
                    tags.add(user.weight + "kg");
                    tags.add(user.age + "세");
                }
                tags.add(User.SEX_STRING[user.sex]);
                text = txtToPrompt(tags, text);
            }

        }else{
            if (lastChat.holder != null) {
                lastChat.holder.tv_op.setVisibility(View.GONE);
                lastChat.holder.showLoad("코디 생성 중...");
            }

            lastChat.state = Chat.STATE_LOAD_TEXT;
            lastChat.text = "";
            lastChat.prompt = "";
            gptChat = lastChat;
        }



        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(OPEN_AI_KEY, Duration.ofSeconds(20))
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

        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), text);

        if (lastChat == null) {
            messages.add(chatMessage);
        }else{
            messages.remove(messages.size()-1);
            messages.add(chatMessage);
        }

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
                Log.d("on fail", throwable.toString());
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
                        editText.setEnabled(false);
                        ChatListAdopter.ViewHolder holder = gptChat.holder;
                        holder.tv_op.setText(gptChat.text);
                        holder.change.setVisibility(View.GONE);
                        holder.tv_feed.setVisibility(View.GONE);
                        holder.tv_op.setVisibility(View.VISIBLE);
                        holder.hideLoad();

                        rc.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                rc.smoothScrollToPosition(adopter.getItemCount()-1);
                            }
                        });
                        ChatActivity.this.isInit = false;
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

                        int p = gptChat.text.indexOf("상의:");
                        if (p != -1){
                            int n = gptChat.text.indexOf("\n", p);
                            if (n != -1){
                                Spannable spannable = (Spannable) gptChat.holder.tv_op.getText();
                                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#2E2E2E")), p, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), p, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new UnderlineSpan(), p, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                gptChat.clothes.put("0", gptChat.text.substring(p+3, n+1));
                            }
                        }

                        int p2 = gptChat.text.indexOf("하의:");
                        if (p2 != -1){
                            int n = gptChat.text.indexOf("\n", p2);
                            if (n != -1){
                                Spannable spannable = (Spannable) gptChat.holder.tv_op.getText();
                                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#2E2E2E")), p2, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), p2, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new UnderlineSpan(), p2, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                gptChat.clothes.put("1", gptChat.text.substring(p2+3, n+1));
                            }
                        }
                        int p3 = gptChat.text.indexOf("신발:");
                        if (p3 != -1){
                            int n = gptChat.text.indexOf("\n", p3);
                            if (n != -1){
                                Spannable spannable = (Spannable) gptChat.holder.tv_op.getText();
                                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#2E2E2E")), p3, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), p3, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new UnderlineSpan(), p3, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                gptChat.clothes.put("2", gptChat.text.substring(p3+3, n+1));
                            }
                        }
                        int p4 = gptChat.text.indexOf("액세서리:");
                        if (p4 != -1){
                            int n = gptChat.text.indexOf("\n", p4);
                            if (n != -1){
                                Spannable spannable = (Spannable) gptChat.holder.tv_op.getText();
                                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#2E2E2E")), p4, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new StyleSpan(Typeface.BOLD), p4, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new UnderlineSpan(), p4, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                gptChat.clothes.put("3", gptChat.text.substring(p4+5, n+1));
                            }
                        }


                        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), gptChat.text);
                        messages.add(chatMessage);

                        if (messages.size() >= 10){
                            messages.clear();
                            ChatActivity.this.isInit = false;
                        }

                        editText.setEnabled(true);

                        if (hasAndKey(gptChat.text, "상의", "하의")){
                            gptChat.isRecommend = true;
                            gptChat.holder.change.setTag(gptChat);
                            gptChat.holder.tv_feed.setVisibility(View.GONE);
                            gptChat.holder.change.setVisibility(View.VISIBLE);
                            gptChat.holder.change.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    v.setVisibility(View.GONE);
                                    Chat c = (Chat) v.getTag();
                                    outFitToPrompt(c);
                                    txtToTag(c);
                                }
                            });
                        }else{
                            gptChat.isRecommend = false;
                            messages.clear();
                            ChatActivity.this.isInit = true;
                        }


                        rc.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                rc.smoothScrollToPosition(getPos(gptChat));
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
                        editText.setEnabled(true);
                        ChatListAdopter.ViewHolder holder = gptChat.holder;
                        holder.tv_feed.setVisibility(View.GONE);
                        holder.change.setVisibility(View.GONE);
                        holder.tv_op.setText("죄송합니다. 오류가 발생했습니다.");
                        holder.tv_op.setVisibility(View.VISIBLE);
                        holder.hideLoad();
                    }
                });

            }

        };

    }

    public static boolean hasAndKey(String value, String... keys) {
        for (String key : keys) {
            if (!value.contains(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if ( pressedTime == 0 ) {
            Toast.makeText(ChatActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis();
        }
        else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > 2000 ) {
                Toast.makeText(ChatActivity.this, " 한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                pressedTime = 0 ;
            }
            else {
                super.onBackPressed();
                finish(); // app 종료 시키기
            }
        }
    }

    public void outFitToPrompt(Chat chat){

        if (chat.holder != null) {
            chat.holder.showLoad("이미지 생성 중...");
            chat.state = Chat.STATE_LOAD_IMAGE;
        }




        String p = "Translate the resulting outfit into an \"English command\" consisting of words and commas. " +
                "For example, you could structure your command as \"keyword1, keyword2, keyword3\".";

        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(OPEN_AI_KEY, Duration.ofSeconds(500))
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
                        chat.holder.hideLoad();
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

    public void txtToTag(Chat chat){

        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(OPEN_AI_KEY, Duration.ofSeconds(500))
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

        final ChatMessage message = new ChatMessage(ChatMessageRole.USER.value(), "지금까지 나눈 코디 대화를 키워드와 쉼표로 구분된 명령어로만 정리해줘. " +
                "예를 들면, 명령어를 이렇게 만들 수 있어. \"키워드1, 키워드2, 키워드3, 키워드4\"");
        List<ChatMessage> list = new ArrayList<>(messages);
        list.add(message);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(list)
                .stream(false)
                .n(1)
                .maxTokens(2048)
                .model("gpt-3.5-turbo")
                .logitBias(new HashMap<>())
                .build();

        GptCallback gptCallback = new GptCallback() {
            @Override
            void onReceived(ChatMessage message) {
                chat.user_tag += message.getContent();
            }

            @Override
            void onComplete() {

            }

            @Override
            void onFail() {

            }

        };


        service.streamChatCompletion(request).subscribeOn(Schedulers.io()).subscribe(chunk -> {
            ChatCompletionChoice choice = chunk.getChoices().get(0);
            if (choice.getFinishReason() != null) {
                gptCallback.onComplete();
                return;
            }
            ChatMessage mes = choice.getMessage();
            if (mes.getContent() != null) {
                gptCallback.onReceived(mes);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                gptCallback.onFail();
            }
        });
    }

    public static byte[] StringToByte(String encodedString) {
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

    public void promptToImage(Chat chat){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .callTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(new BasicAuthInterceptor("kim1914", "q23kkr"))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://f5a4-168-188-128-132.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //JSON 요청 본문 참조 바람. (https://cloud.google.com/vision/docs/ocr?hl=ko#detect_text_in_a_local_image)

        Request request = new Request();
        request.setPrompt(chat.prompt, user.sex);

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);


        Call<Response> call = retrofitService.getPosts(request);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                chat.state = Chat.STATE_COMPLETE_IMAGE;
                if (response.isSuccessful() && response.body() != null){
                    String base = response.body().images.get(0).split(",", 1)[0];
                    byte[] bytes = StringToByte(base);
                    Cody cody = new Cody(user.nickname, user.profile, user.uid, chat.user_tag, chat.prompt);
                    cody.generate(ChatActivity.this, bytes);
                    cody.clothes = chat.clothes;
                    cody.tags = chat.user_tag;
                    chat.cody = cody;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (chat.holder != null) {
                                chat.holder.cdiv.setVisibility(View.VISIBLE);
                                chat.holder.imageView.setVisibility(View.VISIBLE);
                                chat.holder.tv_feed.setVisibility(View.VISIBLE);

                                chat.holder.imageView.setTag(chat);

                                chat.holder.imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Chat c = (Chat) v.getTag();
                                        if (c.cody == null) return;
                                        Intent intent = new Intent(ChatActivity.this, GalleryActivity.class);
                                        intent.putExtra("uri", c.cody.imageURI);
                                        intent.putExtra("tags", c.cody.tags);
                                        startActivity(intent);
                                    }
                                });



                                chat.holder.imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                    @Override
                                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                        rc.smoothScrollToPosition(getPos(chat));
                                    }
                                });
                                Glide.with(ChatActivity.this).load(chat.cody.imageURI).addListener(new RequestListener<Drawable>() {
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
                            chat.holder.hideLoad();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "이미지 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                chat.state = Chat.STATE_COMPLETE_IMAGE;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chat.holder != null) {
                            chat.holder.tv_feed.setVisibility(View.VISIBLE);
                            chat.holder.hideLoad();
                        }
                    }
                });
            }

        });
    }



    class Chat{

        public static final int STATE_LOAD_TEXT = 1; // 유저가 텍스트를 넣었을 때, 로드 중
        public static final int STATE_COMPLETE_TEXT = 2; // 로드가 완료되어서 채팅 또는 코디 텍스트가 만들어졌을 때
        public static final int STATE_LOAD_IMAGE = 3; //
        public static final int STATE_COMPLETE_IMAGE = 4; // 이미지가 완성되었을 때
        public static final int STATE_ERROR = 5; // 에러 발생 시

        String text;
        String userText; // 코디 재생성 시, 유저의 문장들을 재사용
        Map<String, String> clothes = new HashMap<>();
        boolean isGPT;
        boolean isRecommend;
        int state = STATE_LOAD_TEXT;
        String prompt = "";
        String user_tag = "";
        Cody cody;
        int remakeCount = 0; // 재생성 횟수 또는 한도
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

    public int getPos(Chat chat){
        if (chats.contains(chat)) return chats.indexOf(chat);
        return chats.size() - 1;
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
                  holder.change.setVisibility(View.GONE);
                  holder.tv_feed.setVisibility(View.GONE);

                  holder.renew.setTag(chat);
                  holder.tv_feed.setTag(chat);
                  holder.renew.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Chat chat = (Chat) v.getTag();
                          if (chat.remakeCount < 5) {
                              chat.cody.remove(ChatActivity.this);
                              chat.remakeCount++;
                              chat.holder.showLoad("이미지 생성 중...");
                              chat.state = Chat.STATE_LOAD_IMAGE;
                              promptToImage(chat);
                          }else{
                              Toast.makeText(ChatActivity.this, "한 코디당 재생성 횟수는 5번까지 입니다.", Toast.LENGTH_SHORT).show();
                          }
                      }
                  });

                  holder.tv_feed.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Chat chat = (Chat) v.getTag();
                          chat.cody.share(ChatActivity.this);
                          holder.tv_feed.setVisibility(View.GONE);
                      }
                  });

                  chat.holder = holder;

                  if (chat.state == Chat.STATE_LOAD_TEXT || chat.state == Chat.STATE_LOAD_IMAGE){
                      holder.tv_op.setVisibility(View.GONE);

                      if (chat.state == Chat.STATE_LOAD_IMAGE){
                          holder.showLoad("착용샷 생성 중...");
                      }else{
                          holder.showLoad("코디 생성 중...");
                      }
                  }
                  else if (chat.state == Chat.STATE_ERROR) {
                      holder.tv_op.setText("죄송합니다. 오류가 발생했습니다.");
                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.hideLoad();

                  }
                  else if (chat.state == Chat.STATE_COMPLETE_TEXT){

                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.hideLoad();
                      holder.tv_op.setText(chat.text);

                      if (chat.isRecommend) {
                          holder.tv_feed.setVisibility(View.GONE);
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
                      }
                  }else if (chat.state == Chat.STATE_COMPLETE_IMAGE){

                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.tv_op.setText(chat.text);
                      holder.hideLoad();
                      holder.tv_feed.setVisibility(View.VISIBLE);
                      holder.change.setVisibility(View.GONE);
                      holder.change.setTag(chat);
                      holder.cdiv.setVisibility(View.VISIBLE);
                      holder.imageView.setTag(chat);

                      holder.imageView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Chat c = (Chat) v.getTag();
                              if (c.cody == null) return;
                              Intent intent = new Intent(ChatActivity.this, GalleryActivity.class);
                              intent.putExtra("uri", c.cody.imageURI);
                              intent.putExtra("tags", c.cody.tags);
                              startActivity(intent);
                          }
                      });


                      Glide.with(ChatActivity.this).load(chat.cody.imageURI).addListener(new RequestListener<Drawable>() {
                          @Override
                          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                              return false;
                          }

                          @Override
                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                              return false;
                          }
                      }).into(holder.imageView);

                  }
                  else{
                      holder.tv_op.setVisibility(View.VISIBLE);
                      holder.tv_op.setText(chat.text);
                      holder.hideLoad();
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

            RelativeLayout load;

            TextView tv_op;
            TextView tv_my;

            TextView tv_feed;

            LottieAnimationView loadView;
            TextView tv_load;
            ImageView imageView;
            ImageView renew;
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
                load = itemView.findViewById(R.id.load);
                renew = itemView.findViewById(R.id.fav);
                tv_feed = itemView.findViewById(R.id.remake);
            }


            public void showLoad(String text){
                load.setVisibility(View.VISIBLE);
                loadView.setVisibility(View.VISIBLE);
                tv_load.setVisibility(View.VISIBLE);
                tv_load.setText(text);
                loadView.playAnimation();
                tv_feed.setVisibility(View.GONE);
                cdiv.setVisibility(View.GONE);
            }

            public void hideLoad(){
                load.setVisibility(View.GONE);
                loadView.setVisibility(View.GONE);
                tv_load.setVisibility(View.GONE);
                loadView.cancelAnimation();
            }
        }


    }




}