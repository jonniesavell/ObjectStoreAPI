package com.indigententerprises.components;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jonniesavell
 */
public class MetaDataServiceImplementation {

    public Map<String, Object> deserializeMetaData(final InputStream inputStream) throws IOException {

        final Map<String, Object> result = new HashMap<>();

        final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        final DataFileStream<GenericRecord> dataReader = new DataFileStream<>(inputStream, datumReader);

        try {
            // this is how you read the schema out of the file
            final Schema schema = dataReader.getSchema();

            // this is how you retrieveObjectMetaData the fields from the schema
            final Collection<Schema.Field> schemaFields = schema.getFields();

            for (final Schema.Field schemaField : schemaFields) {
                System.out.println("name        : " + schemaField.name());
                System.out.println("schema-type : " + schemaField.schema().getType());
                System.out.println("position    : " + schemaField.pos());
            }

            while (dataReader.hasNext()) {

                final GenericRecord attributeData = dataReader.next();
                final Utf8 utf8 = (Utf8) attributeData.get("attributeName");
                result.put(utf8.toString(), attributeData.get("attributeValue"));
            }

            return result;
        } finally {
            dataReader.close();
        }
    }
}
