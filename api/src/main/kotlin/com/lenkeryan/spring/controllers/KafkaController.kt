package com.lenkeryan.spring.controllers

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.KeyValueIterator
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.Stores
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/kafka")
class KafkaController(
    private val kafkaStreamsFactory: StreamsBuilderFactoryBean,
) {

    @GetMapping
    fun all(): Any {
        val managerCoordinates: MutableList<String> = ArrayList()

//        val countStoreSupplier = Stores.keyValueStoreBuilder(
//            Stores.persistentKeyValueStore("managers-coordinates"),
//            Serdes.String(),
//            Serdes.String()
//        )
//        val countStore = countStoreSupplier.build()

        val store: ReadOnlyKeyValueStore<String, String> = kafkaStreamsFactory
            .kafkaStreams
            .store<ReadOnlyKeyValueStore<String, String>>(
                StoreQueryParameters.fromNameAndType(
                    "managers",
                    QueryableStoreTypes.keyValueStore()
                )
            )
        val it: KeyValueIterator<String, String> = store.all()
        it.forEachRemaining { kv: KeyValue<String?, String> ->
            managerCoordinates.add(
                kv.value
            )
        }
        return managerCoordinates
    }
}