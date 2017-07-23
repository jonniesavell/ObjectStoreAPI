package com.indigententerprises.components;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.util.Utf8;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jonniesavell
 */
public class MetaDataServiceImplementation {

    /**
     * attributes must be non-null
     * outputStream must be non-null and must correspond to a live stream
     *   that was truncated prior to invocation
     * @param outputStream
     * @param attributes
     * @throws IOException
     */
    public void serializeMetaData(
            final OutputStream outputStream,
            final Map<String, Object> attributes) throws IOException {

        final Schema ratingSchema = SchemaBuilder.record("Attribute")
                .fields()
                .name("attributeName").type().stringType().noDefault()
                .name("attributeType").type().stringType().noDefault()
                .name("attributeValue").type().nullable().intType().noDefault()
                .endRecord();
        final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(ratingSchema);
        final DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        try {
            dataFileWriter.create(ratingSchema, outputStream);

            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                final GenericRecordBuilder recordBuilder =
                        new GenericRecordBuilder(ratingSchema)
                                .set("attributeName", entry.getKey())
                                .set("attributeType", entry.getValue().getClass().getName())
                                .set("attributeValue", entry.getValue());
                final GenericRecord attributeRecord = recordBuilder.build();
                dataFileWriter.append(attributeRecord);
            }
        } finally {
            dataFileWriter.close();
        }
    }

    /**
     * inputStream must be non-null and must correspond to a live stream
     *   populated with data that this service recognizes
     */
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
