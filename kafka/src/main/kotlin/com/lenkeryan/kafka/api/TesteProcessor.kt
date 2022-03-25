//package api
//
//import org.apache.kafka.common.serialization.Serde
//import org.apache.kafka.common.serialization.Serdes
//import org.apache.kafka.streams.kstream.*
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//
//import java.util.*
//
//
//@Component
//class TesteProcessor {
//
//    private val  STRING_SERDE: Serde<String> = Serdes.String()
//
//    @Autowired
//    void buildPipeline(streamsBuilder: StreamsBuilder) {
////        val messageStream: KStream<String, String> = streamsBuilder
////            .stream("input-topic", Consumed.with(STRING_SERDE, STRING_SERDE))
////
////        val wordCounts = messageStream
////            .mapValues(ValueMapper { obj: String ->
////                obj.lowercase(
////                    Locale.getDefault()
////                )
////            } as ValueMapper<String, String>)
////            .flatMapValues<Any> { value: String ->
////                Arrays.asList(
////                    value.split("\\W+".toRegex()).toTypedArray()
////                )
////            }
////            .groupBy(KeyValueMapper { key: String?, word: Any? -> word }, Grouped.with(STRING_SERDE, STRING_SERDE))
////            .count()
////
////        wordCounts.toStream().to("output-topic")
//    }
//}