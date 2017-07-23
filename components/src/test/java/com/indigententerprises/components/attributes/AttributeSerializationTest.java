package com.indigententerprises.components.attributes;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
import org.junit.Test;

import java.io.File;

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

        Schema ratingSchema = SchemaBuilder.record("Rating")
                .fields()
                .name("userId").type().intType().noDefault()
                .name("movieId").type().intType().noDefault()
                .name("rating").type().intType().noDefault()
                .name("timeInSeconds").type().intType().noDefault()
                .endRecord();

        GenericRecordBuilder recordBuilder =
                new GenericRecordBuilder(ratingSchema)
                .set("userId", 1)
                .set("movieId", 3)
                .set("rating", 5)
                .set("timeInSeconds", 1000);

        GenericRecord record = recordBuilder.build();
        File file = new File("users.avro");
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(ratingSchema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        try {
            dataFileWriter.create(ratingSchema, file);
            dataFileWriter.append(record);
        } finally {
            dataFileWriter.close();
        }
    }
}
