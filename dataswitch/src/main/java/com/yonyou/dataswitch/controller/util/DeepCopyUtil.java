package com.yonyou.dataswitch.controller.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class DeepCopyUtil {
    public static <K, V> Map<K, V> deepCopy(Map<K, V> original) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(original);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Map<K, V>) ois.readObject();
    }
//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        Map<String, Integer> originalMap = new HashMap<>();
//        originalMap.put("key1", 1);
//        originalMap.put("key2", 2);
//        Map<String, Integer> clonedMap = deepCopy(originalMap);
//        System.out.println(clonedMap);
//    }
}
