package com.github.davidhoyt.playground.learn;

import java.util.ArrayList;

public class FoldLeftJava {
    public static void main(String[] args) {
        new FoldLeftJava().doSomething();
    }

    private void doSomething() {
        final ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);

        int sum = 0;
        for (int i = 0; i < list.size(); ++i) {
            sum += list.get(i);
        }

        System.out.println(sum);
    }
}
