package edu.nsbm.phishguard.util.client;

import edu.nsbm.phishguard.enums.Type;
import edu.nsbm.phishguard.util.response.LlmResponse;

public interface LlmClient {
    LlmResponse generateMail(Type emailType);
}
