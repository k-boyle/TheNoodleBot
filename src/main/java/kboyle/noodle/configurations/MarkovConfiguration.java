package kboyle.noodle.configurations;

import com.google.gson.Gson;
import kboyle.noodle.NoodleBotConfig;
import kboyle.noodle.markov.MarkovChain;
import kboyle.noodle.markov.MarkovReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarkovConfiguration {
    @Bean
    public MarkovReader markovReader(Gson gson) {
        return new MarkovReader(gson);
    }

    @Bean
    public MarkovChain markovChain(MarkovReader reader, NoodleBotConfig config) {
        return MarkovChain.create(reader, config.getMarkov());
    }
}
