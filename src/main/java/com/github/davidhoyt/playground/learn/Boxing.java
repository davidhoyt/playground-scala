package com.github.davidhoyt.playground.learn;

import java.util.ArrayList;

public class Boxing {
    public static void main(String[] args) {
        int i = 0;
        Integer boxed = Integer.valueOf(i);

        boolean areTheseEqual = 0 == 0;
        boolean areTheseEqual2 = Integer.valueOf(0) == Integer.valueOf(0);
        boolean areTheseEqual3 = Integer.valueOf(0).equals(Integer.valueOf(0));

        for (int idx = 0; idx < 150; ++idx)
            System.out.println(idx + ": " + (Integer.valueOf(idx) == (Integer.valueOf(idx))));

        short s = (short)0;
        Short boxedS = s;

        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(0);
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(boxed);

        for (Integer value : arrayList)
            System.out.println(value);
    }
}
