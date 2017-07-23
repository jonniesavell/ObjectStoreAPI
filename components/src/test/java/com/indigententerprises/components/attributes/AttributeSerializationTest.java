package com.indigententerprises.components.attributes;

import com.indigententerprises.components.MetaDataServiceImplementation;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

/**
 *
 * test the use of Avro without code generation
 *
 * @author jonniesavel
 *
 */
public class AttributeSerializationTest {

    @Test
    public void test() throws Exception {

        //
        // object-metatdata service, initialization
        //
        // this is how you create the schema the first time
        Schema ratingSchema = SchemaBuilder.record("Rating")
                .fields()
                .name("attributeName").type().stringType().noDefault()
                .name("attributeType").type().stringType().noDefault()
                .name("attributeValue").type().nullable().intType().noDefault()
                .endRecord();

        //
        // object-metatdata service, persist
        //
        // this is how you create a record without code generation
        final GenericRecordBuilder recordBuilder1 =
                new GenericRecordBuilder(ratingSchema)
                        .set("attributeName", "pants")
                        .set("attributeType", "Integer")
                        .set("attributeValue", 5);

        final GenericRecord attributeRecord1 = recordBuilder1.build();

        final GenericRecordBuilder recordBuilder2 =
                new GenericRecordBuilder(ratingSchema)
                        .set("attributeName", "socks")
                        .set("attributeType", "Integer")
                        .set("attributeValue", null);

        final GenericRecord attributeRecord2 = recordBuilder2.build();

        //
        // object-metatdata service, persist
        //
        // this is how you persist both the schema and a record
        File file = new File("metadata.avro");
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(ratingSchema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        try {
            dataFileWriter.create(ratingSchema, file);
            dataFileWriter.append(attributeRecord1);
            dataFileWriter.append(attributeRecord2);
        } finally {
            dataFileWriter.close();
        }

        final FileInputStream inputStream =
                new FileInputStream(file);
        final MetaDataServiceImplementation systemUnderTest =
                new MetaDataServiceImplementation();
        final Map<String, Object> result =
                systemUnderTest.deserializeMetaData(inputStream);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);

        for (final Map.Entry<String, Object> entry : result.entrySet()) {
            System.out.println("(" + entry.getKey() + ", " + entry.getValue() + ")");
        }
    }
}
