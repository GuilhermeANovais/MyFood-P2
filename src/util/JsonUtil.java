package util;

import java.io.*;
import java.util.*;

public class JsonUtil {

    // Read entire file content
    public static String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    // Write content to file
    public static void writeFile(String path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);
        writer.close();
    }

    // Parse simple value (String without nested objects)
    public static String parseString(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) return null;
        start += searchKey.length();
        // Skip whitespace
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\t')) start++;
        if (start >= json.length()) return null;

        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        }
        // Number
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
            end++;
        }
        return json.substring(start, end).trim();
    }

    // Parse integer value
    public static int parseInt(String json, String key) {
        String value = parseString(json, key);
        if (value == null) return 0;
        return Integer.parseInt(value);
    }

    // Parse float value
    public static float parseFloat(String json, String key) {
        String value = parseString(json, key);
        if (value == null) return 0f;
        return Float.parseFloat(value);
    }

    // Parse boolean value
    public static boolean parseBoolean(String json, String key) {
        String value = parseString(json, key);
        if (value == null) return false;
        return Boolean.parseBoolean(value);
    }

    // Parse array of strings
    public static List<String> parseStringArray(String json, String key) {
        List<String> list = new ArrayList<>();
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) return list;
        start += searchKey.length();
        while (start < json.length() && json.charAt(start) != '[') start++;
        if (start >= json.length()) return list;
        start++; // skip '['

        int end = json.indexOf(']', start);
        if (end == -1 || start >= end) return list;

        String arrayContent = json.substring(start, end).trim();
        if (arrayContent.isEmpty()) return list;

        // Parse strings with possible spaces
        int i = start;
        while (i < end) {
            // Skip whitespace and commas
            while (i < end && (json.charAt(i) == ' ' || json.charAt(i) == '\t' || json.charAt(i) == ',')) i++;
            if (i >= end) break;
            if (json.charAt(i) == '"') {
                i++;
                int strStart = i;
                while (i < end && json.charAt(i) != '"') i++;
                list.add(json.substring(strStart, i));
                i++; // skip closing quote
            }
        }
        return list;
    }

    // Escape string for JSON
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Parse array of integers
    public static List<Integer> parseIntArray(String json, String key) {
        List<Integer> list = new ArrayList<>();
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) return list;
        start += searchKey.length();
        while (start < json.length() && json.charAt(start) != '[') start++;
        if (start >= json.length()) return list;
        start++; // skip '['

        int end = json.indexOf(']', start);
        if (end == -1 || start >= end) return list;

        String arrayContent = json.substring(start, end).trim();
        if (arrayContent.isEmpty()) return list;

        String[] parts = arrayContent.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                list.add(Integer.parseInt(trimmed));
            }
        }
        return list;
    }

    // Parse array of objects (returns list of JSON object strings)
    public static List<String> parseObjectArray(String json, String key) {
        List<String> list = new ArrayList<>();
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) return list;
        start += searchKey.length();
        while (start < json.length() && json.charAt(start) != '[') start++;
        if (start >= json.length()) return list;
        start++; // skip '['

        int braceCount = 0;
        int objStart = -1;

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (braceCount == 0) objStart = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && objStart >= 0) {
                    list.add(json.substring(objStart, i + 1));
                    objStart = -1;
                }
            }
        }
        return list;
    }

    // Build JSON integer array
    public static String buildIntArray(List<Integer> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
