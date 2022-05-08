package edu.ucsd.cse110.team56.zooseeker.misc;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JsonReader {
    /**
     * Parse JSON *LIST* from a file
     * @param context Application context
     * @param file path to the file
     * @param clazz destination type
     * @param <T> type
     * @return a list of objects T
     */
    public static <T> List<T> parseJsonList(Context context, String file, Class<T> clazz) {
        try {
            InputStream input = context.getAssets().open(file);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();

            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            List<T> results = gson.fromJson(reader, type);

            Log.d("JsonParser", results.toString());
            return results;
        } catch (IOException e) {
            Log.d("JsonParser", file, e);
            return Collections.emptyList();
        }
    }

    /**
     * Parse JSON *OBJECT* from a file
     * @param context Application context
     * @param file path to the file
     * @param clazz destination type
     * @param <T> type
     * @return object T
     */
    public static <T> Optional<T> parseJson(Context context, String file, Class<T> clazz) {
        try {
            InputStream input = context.getAssets().open(file);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();

            T result = gson.fromJson(reader, clazz);
            Log.d("JsonParser", result.toString());
            return Optional.of(result);
        } catch (IOException e) {
            Log.d("JsonParser", file, e);
            return Optional.empty();
        }
    }
}
