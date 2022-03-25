package com.lenkeryan.spring.topology

import com.lenkeryan.spring.models.ManagerCoordinates
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.springframework.kafka.support.serializer.JsonSerde


class ApplicationTopology {

    companion object {
        val MANAGERS_COORDINATES = "managers-coordinates"
        val MANAGERS = "managers"

        fun buildTopology(): Topology {
            val managersCoordinateSerde: Serde<ManagerCoordinates> = JsonSerde(ManagerCoordinates::class.java)
            val streamsBuilder = StreamsBuilder()
            val managersCoordinatesStream: KStream<String, ManagerCoordinates> = streamsBuilder.stream(
                MANAGERS_COORDINATES,
                Consumed.with(Serdes.String(), managersCoordinateSerde)
            )

            managersCoordinatesStream
                .to(MANAGERS, Produced.with(Serdes.String(), managersCoordinateSerde))

            return streamsBuilder.build()
        }
    }
}