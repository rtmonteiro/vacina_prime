package com.ufes.patricia.twitter
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import models.Temperature
//import org.apache.kafka.common.errors.SerializationException
//import org.apache.kafka.common.serialization.Serializer
//import java.nio.charset.StandardCharsets
//
//class TemperatureSerializer: Serializer<Temperature?> {
//    private val objectMapper: ObjectMapper = ObjectMapper()
//    override fun configure(configs: Map<*, *>?, isKey: Boolean) {
//        //Serializer.super.configure(configs, isKey);
//    }
//
//    override fun serialize(s: String, o: Tweet?): ByteArray {
//        return try {
//            if (o == null) {
//                println("Null received at serializing")
//                return null
//            }
//            //System.out.println(o);
//            println("Serializing...")
//            objectMapper.writeValueAsString(o).getBytes(StandardCharsets.UTF_8)
//        } catch (e: Exception) {
//            throw SerializationException("Error when serializing MessageDto to byte[]")
//        }
//    }
//
//    override fun close() {
//        //  Serializer.super.close();
//    }
////}