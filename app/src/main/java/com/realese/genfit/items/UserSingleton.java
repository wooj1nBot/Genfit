package com.realese.genfit.items;

import android.content.Context;

public class UserSingleton {
    private static User userInstance;

    private UserSingleton() {
        // private 생성자로 외부에서의 인스턴스 생성 방지
    }

    public static User getInstance(Context applicationContext) {
        return userInstance;
    }

    public static User getInstance(Context context, String sex) {
        if (userInstance == null) {
            // User 객체가 아직 생성되지 않았을 때만 인스턴스 생성
            userInstance = new User(context, sex);
        }
        return userInstance;
    }

    public static User getInstance(Context context, int height, int weight, int age, String sex) {
        if (userInstance == null || userInstance.isGUEST()) {
            // User 객체가 아직 생성되지 않았을 때만 인스턴스 생성
            // 그리고 게스트일 때
            // 덮어쓰기 기능 만들기
            userInstance = new User(context, sex);
        }
        return userInstance;
    }


}