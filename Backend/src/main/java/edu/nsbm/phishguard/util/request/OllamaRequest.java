package edu.nsbm.phishguard.util.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
@Builder
public class OllamaRequest {
    private String model;
    private boolean stream;
    private String format;
    private boolean think;
    private List<Message> messages;
    private Options options;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    @Builder
    public static class Options {
        private double temperature;

        @JsonProperty("num_predict")
        private int numPredict;
    }
}
