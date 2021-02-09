package kboyle.noodle.markov;

import kboyle.noodle.NoodleBotConfig.MarkovConfig;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarkovChain {
    private MarkovChain() {

    }

    public static MarkovChain create(MarkovReader reader, MarkovConfig config) {
        MarkovMessage[] markovMessages = reader.readMessages(config.getFile());
        Map<Long, Map<String, Map<String, Integer>>> collect = Arrays.stream(markovMessages)
            .collect(Collectors.groupingBy(MarkovMessage::userId))
            .entrySet()
            .stream()
            .map(entry -> {
                List<MarkovMessage> messages = entry.getValue();
                Map<String, Map<String, Integer>> chainCounts = new HashMap<>();
                for (MarkovMessage message : messages) {
                    String content = message.content();
                    String[] splitContent = content.split(" ");
                    for (int i = 0; i < splitContent.length; i++) {
                        String key = i == 0 ? null : splitContent[i];
                        String value = i == splitContent.length - 1 ? null : splitContent[i + 1];
                        chainCounts.compute(key, (k, map) -> {
                            if (map == null) {
                                map = new HashMap<>();
                            }

                            map.compute(value, (v, count) -> {
                                if (count == null) {
                                    count = 0;
                                }

                                return count + 1;
                            });

                            return map;
                        });
                    }
                }

                return new AbstractMap.SimpleEntry<>(entry.getKey(), chainCounts);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        return new MarkovChain();
    }
}
