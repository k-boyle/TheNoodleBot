package kboyle.noodle.markov;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;

public class MarkovReader {
    private final Gson gson;

    public MarkovReader(Gson gson) {
        this.gson = gson;
    }

    public MarkovMessage[] readMessages(String file) {
        try {
            File jsonFile = ResourceUtils.getFile("classpath:" + file);
            try (FileReader fileReader = new FileReader(jsonFile); JsonReader jsonReader = new JsonReader(fileReader)) {
                return gson.fromJson(jsonReader, MarkovMessage[].class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
