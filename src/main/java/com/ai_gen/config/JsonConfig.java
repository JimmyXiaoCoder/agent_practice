package com.ai_gen.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Spring MVC Json 配置
 */
@JsonComponent
public class JsonConfig {

    /**
     * Long -> String 序列化
     */
    public static class LongJsonSerializer extends JsonSerializer<Long> {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            // 将 Long 值转为字符串
            gen.writeString(value.toString());
        }
    }

//    /**
//     * 反序列化：String -> Long
//     */
//    public static class StringToLongJsonDeserializer extends JsonDeserializer<Long> {
//        @Override
//        public Long deserialize(JsonParser p, DeserializationContext ctxt)
//                throws IOException {
//            String text = p.getText();
//            if (text == null || text.isEmpty()) {
//                return null;
//            }
//            try {
//                return Long.parseLong(text);
//            } catch (NumberFormatException e) {
//                throw new JsonParseException(p, "Cannot parse long from: " + text);
//            }
//        }
//    }
}
