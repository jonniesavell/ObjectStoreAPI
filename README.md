# ObjectStoreAPI

This is a personal project to produce an API to store and retrieve application configuration using cloud object storage.

I began with a simple proof of concept for downloading and uploading from AWS S3. Consequently, the first provider will use AWS S3 although the interface will eventually be independent of cloud vendor.

The second phase will the implementation of a simple proof of concept for downloading and uploading from CGD.

The third phase will be the introduction of metadata into the POCs. It is envisioned that client programs will consult the metadata in order to determine the type of an object retrieved. While it is inconceivable that a client could interrogate an object in the event that the object's type is something other than that anticipated, it is at least beneficial to ensure that the object is of a certain type before the interrogation proceeds. While I seek something like a schema or a programming language type, I will doubtless find something much less satisfying.

The fourth phase will be the introduction of native object/role/permissions into the POCs.

The fifth and final phase of the POC will be the creation of an abstraction of the object/role/permissions and metadata schemes that map perfectly to the native object/role/permissions and metadata schemes offered by the AWS and GCD providers as well as the interfaces adorned by those providers.

Subsequent modifications to this project involve changes in changes to required to handle temporary problems in communication between the providers and the object store.

# Observations

The cost of partial vendor independence is the maintenance of abstractions that must be modified should we encounter a new cloud vendor's service that we wish to accommodate. In the event that a new cloud vendor's service is selected for accommodation, such modification may (and likely will) require modifications that involve more than simple structural additions to the domain and, therefore, will either impact clients unless proper versioning is employed. My stated preference is embedded package versioning at the outset with the understanding that this results in either greater library size or additional source code repositories.

With regard to security and object/role/permissions, it is clear that I will eventually confront the realization that I am ill-equiped to handle this problem. Nonetheless, there would be no learning if I were to avoid it.
