# ObjectStoreAPI

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

This is a personal project to produce an API to store and retrieve application configuration using cloud object storage, the abstraction over competing vendor offerings: every junior software developer's fantasy.

I began with a simple proof of concept for downloading and uploading from AWS S3. Consequently, the first provider will use AWS S3 although the interface will eventually be independent of cloud vendor.

The second phase will be the introduction of metadata into the POCs. It is envisioned that client programs will consult the metadata in order to find attributes of the object retrieved. While it is inconceivable that a client could interrogate an object in the event that the object's type is something other than that anticipated, it is at least beneficial to ensure that the object is of a certain type before the interrogation proceeds. Therefore, object-type should reside within these attributes. While I seek something like a schema or a programming language type, I will doubtless find something much less satisfying.

Update: I employed Apache Avro to provide typed attributes. The cost is the small schema embedded into an object for each principle object stored. While the decision to store attributes within a separate S3 object may be controversial because it results in two reads rather than one, it was necessitated by the fact that S3 doesn't allow access to object metadata without retrieval of the associated object. Please see the discussion of this at the bottom of the page.

The third and final phase of this project will result in the implementation of a simple proof of concept for downloading and uploading from Google Cloud Datastore.

Subsequent modifications to this project involve changes in changes to required to handle temporary problems in communication between the providers I have created and the object store.

# Observations

The cost of partial vendor independence is the maintenance of abstractions that must be modified should we encounter a new cloud vendor's service that we wish to accommodate. In the event that a new cloud vendor's service is selected for accommodation, such modification may (and likely will) require modifications that involve more than simple structural additions to the domain and, therefore, will either impact clients unless proper versioning is employed. My stated preference is embedded package versioning at the outset with the understanding that this results in either greater library size or additional source code repositories.

Finally, there is a concern that S3 allows metadata to be set only during the insertion or update of the object to which the metadata is attached. In otherwords, S3 does not allow object metadata to be updated independently of the update of the associated object. This is fine if metadata is set only once (and there is no uncertainty in the values assigned). Given the enormous size of typical S3 data, the cost of object metadata is made exhorbitant, however. The simplest workaround is to place metadata within another object.
