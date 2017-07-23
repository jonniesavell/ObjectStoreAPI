package com.indigententerprises.components.attributes;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

import org.junit.Test;

import java.io.File;
import java.util.Collection;

/**
 *
 * test the use of
 *
 * @author jonniesavel
 *
 */
public class AttributeSerializationTest {

    @Test
    public void test() throws Exception {

        // this is how you create the schema the first time
        Schema ratingSchema = SchemaBuilder.record("Rating")
                .fields()
                .name("userId").type().intType().noDefault()
                .name("movieId").type().intType().noDefault()
                .name("rating").type().intType().noDefault()
                .name("timeInSeconds").type().intType().noDefault()
                .endRecord();

        // this is how you create a record without code generation
        GenericRecordBuilder recordBuilder =
                new GenericRecordBuilder(ratingSchema)
                .set("userId", 1)
                .set("movieId", 3)
                .set("rating", 5)
                .set("timeInSeconds", 1000);

        GenericRecord record = recordBuilder.build();

        // this is how you persist both the schema and a record
        File file = new File("movies.avro");
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(ratingSchema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        try {
            dataFileWriter.create(ratingSchema, file);
            dataFileWriter.append(record);
        } finally {
            dataFileWriter.close();
        }

        // this is how you read the file
        final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        final DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, datumReader);

        // this is how you read the schema out of the file
        final Schema schema = dataFileReader.getSchema();

        // this is how you retrieve the fields from the schema
        final Collection<Schema.Field> schemaFields = schema.getFields();

        for (final Schema.Field schemaField : schemaFields) {

            // these are the properties you'll need to interrogate
            System.out.println("name        : " + schemaField.name());
            System.out.println("schema-type : " + schemaField.schema().getType());
            System.out.println("position    : " + schemaField.pos());
        }

        try {
            GenericRecord movieData = null;

            while (dataFileReader.hasNext()) {
                movieData = dataFileReader.next(movieData);
                System.out.println(movieData);
            }
        } finally {
            dataFileReader.close();
        }
    }
}
