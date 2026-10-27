package com.inklusport.model;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ActividadMongoCodec implements Codec<ActividadMongo> {
    
    @Override 
    public void encode(
            BsonWriter writer,
            ActividadMongo actividad,
            EncoderContext encoderContext) {
        
        writer.writeStartDocument();

        if (actividad.id != null) {
            writer.writeString("_id", actividad.id);
        }

        writer.writeString("nombre", actividad.nombre);
        writer.writeString("deporte", actividad.deporte);

        if (actividad.capacidad == null) {
            writer.writeNull("capacidad");
        } else {
            writer.writeInt32("capacidad", actividad.capacidad);
        }

        writer.writeEndDocument();
    } 

    @Override
    public ActividadMongo decode(
            BsonReader reader,
            DecoderContext decoderContext) {

        reader.readStartDocument();

        String id = null;
        String nombre = null;
        String deporte = null;
        Integer capacidad = null;

        while (reader.readBsonType()
                != org.bson.BsonType.END_OF_DOCUMENT) {
        
            String campo = reader.readName();

            switch (campo) {
                case "_id" -> {
                    if (reader.getCurrentBsonType()
                            == org.bson.BsonType.OBJECT_ID) {
                        id = reader.readObjectId().toHexString();
                    } else if (reader.getCurrentBsonType()
                            == org.bson.BsonType.STRING) {
                        id = reader.readString();
                    } else {
                        reader.skipValue();
                    }
                }
                case "nombre" -> nombre = reader.readString();
                case "deporte" -> deporte = reader.readString();
                case "capacidad" -> {
                    if (reader.getCurrentBsonType()
                            == org.bson.BsonType.INT32) {
                        capacidad = reader.readInt32();
                    } else if (reader.getCurrentBsonType()
                            == org.bson.BsonType.INT64) {
                        capacidad = Math.toIntExact(reader.readInt64());
                    } else if (reader.getCurrentBsonType()
                            == org.bson.BsonType.NULL) {
                        reader.readNull();
                    } else {
                        reader.skipValue();
                    }
                }
                default -> reader.skipValue();
            }
        }

        reader.readEndDocument();

        return new ActividadMongo(id, nombre, deporte, capacidad);       
    } 

    @Override 
    public Class<ActividadMongo> getEncoderClass() {
        return ActividadMongo.class;
    }
}
