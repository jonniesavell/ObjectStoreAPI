# ObjectStoreAPI

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

This is a personal project to produce an API to store and retrieve application configuration using cloud object storage, the abstraction over competing vendor offerings: every junior software developer's fantasy.

I began with a simple proof of concept for downloading and uploading from AWS S3. Consequently, the first provider will use AWS S3 although the interface will eventually be independent of cloud vendor.

The second phase will the implementation of a simple proof of concept for downloading and uploading from Google Cloud Datastore.

The third phase will be the introduction of metadata into the POCs. It is envisioned that client programs will consult the metadata in order to determine the type of an object retrieved. While it is inconceivable that a client could interrogate an object in the event that the object's type is something other than that anticipated, it is at least beneficial to ensure that the object is of a certain type before the interrogation proceeds. While I seek something like a schema or a programming language type, I will doubtless find something much less satisfying.

The fourth phase will be the introduction of native object/role/permissions into the POCs.

The fifth and final phase of the POC will be the creation of an abstraction of the object/role/permissions and metadata schemes that map perfectly to the native object/role/permissions and metadata schemes offered by the AWS and GCD providers as well as the interfaces adorned by those providers.

Subsequent modifications to this project involve changes in changes to required to handle temporary problems in communication between the providers I have created and the object store.

# Observations

The cost of partial vendor independence is the maintenance of abstractions that must be modified should we encounter a new cloud vendor's service that we wish to accommodate. In the event that a new cloud vendor's service is selected for accommodation, such modification may (and likely will) require modifications that involve more than simple structural additions to the domain and, therefore, will either impact clients unless proper versioning is employed. My stated preference is embedded package versioning at the outset with the understanding that this results in either greater library size or additional source code repositories.

With regard to security and object/role/permissions, it is clear that I will eventually confront the realization that I am ill-equiped to handle this problem. Additionally, these issues may simply manifest themselves in the attributes of the cloud user on behalf of whom the program acts rather than anything revealed within this API.

Finally, there is a concern that S3 allows metadata to be set only during the insertion or update of the object to which the metadata is attached. In otherwords, S3 does not allow object metadata to be updated independently of the update of the associated object. This is fine if metadata is set only once (and there is no uncertainty in the values assigned). Given the enormous size of typical S3 data, the cost of object metadata is made exhorbitant, however. The simplest workaround is to place metadata within another object.