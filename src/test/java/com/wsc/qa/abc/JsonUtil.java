package com.wsc.qa.abc;
import java.io.IOException;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public final class JsonUtil
{
    private static final ObjectMapper mapper;

    private JsonUtil() {
        throw new UnsupportedOperationException();
    }

    public static String toJson(final Object obj) throws IOException {
        JsonUtil.mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        JsonUtil.mapper.disable(new DeserializationConfig.Feature[] { DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES });
        JsonUtil.mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        return JsonUtil.mapper.writeValueAsString(obj);
    }

    static {
        mapper = new ObjectMapper();
    }
}
