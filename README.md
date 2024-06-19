
# TASK
## Content
 - [Understand the Requirements](#understand-the-requirements)
 - [Identify Issues](#identify-issues)
 - [Design API](#design-api)
 - [Documentation](#documentation)
 - [Implement API](#implement-api)

## Understand the Requirements:
We should design an API that allows integrators to upload and download binary data to and from MinIO.
### Data Organization:
 Data should be stored in multiple buckets based on data type (fingerprints, faces,
attachments, videos).
### API Functionalities:
1. Initiate Upload:
- An API endpoint should provide pre-signed URLs for file uploads. 
- The response should include URLs and metadata for direct upload to
MinIO.
- The API should determine the appropriate bucket based on the uploaded
file type.
2. Upload:
- Integrators should upload binary data to MinIO using the pre-signed
URLs.
- Pre-signed URLs should be secure and have a limited expiration time.
3. Download:
- Integrators should access binary data using pre-signed URLs
- Pre-signed URLs should be secure and have a limited expiration time.


### Security:
- We should secure the API with JWT.
- We should have endpoints for user registration and authentication.
- Unauthorized users should not be able to access the API from the Minio Storage API.
- We should have a temporary bucket named temp for storing binary data.

## Identify Issues:

### Technical Issues:

### Database for Metadata<br/>
For example , If we get a fingerprint binary file from the user, we should save the fingerprint file info to the database.
We can use One-toMany relationship between the user and the file to associate the file with the user.<br/>

```postgresql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE files_minio (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id), -- User who uploaded the file
    file_name VARCHAR(255) NOT NULL, -- Name of the file
    bucket_name VARCHAR(255) NOT NULL, -- Name of the bucket
    presigned_url VARCHAR(255) NOT NULL, -- Pre-signed URL for file upload/download
    presigned_url_type VARCHAR(50) NOT NULL, -- Type of pre-signed URL (upload/download)
    url_expires_at TIMESTAMP NOT NULL, -- Expiration time for the pre-signed URL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Last updated timestamp
    deleted_at TIMESTAMP -- Soft delete timestamp
);
```


### Authorization with Pre-Signed URLs<br/>
1. Include User Information in Pre-Signed URLs<br/>
   Include user information (e.g., user ID) directly within the pre-signed URL. This approach simplifies implementation but might expose user information in the URL itself.<br/>
2. Validate User Information in Pre-Signed URLs<br/>
   Validate user information (e.g., user ID) in the pre-signed URL before allowing access to the resource. This approach adds an extra layer of security but requires additional validation logic.<br/>
3. Use JWT Tokens with Pre-Signed URLs<br/>
   Use JWT tokens with pre-signed URLs to authenticate and authorize users. This approach provides a secure and scalable solution but requires token management and validation.<br/>

### Optimize Database with Indexes<br/>
  We can optimize the database performance by creating indexes on the columns that are frequently used in queries. 
  By indexing the user ID.
  Use case:
    - When a user requests to download a file, we can quickly retrieve the file metadata by searching for the user ID and file name in the database.

### Use Proxy to restrict number of requests to the our API<br/>
  We can use a proxy server to restrict the number of requests to the API. By limiting the number of requests per user, we can prevent abuse and ensure fair usage of the API resources.<br/>
  For example:
  - We can set a rate limit on the number of requests per user per minute.
  - If a user exceeds the rate limit, the proxy server can return an error response or block the user from making further requests.
  - We can use a proxy server like NGINX or HAProxy to implement rate limiting and protect the API from abuse.

### Use User roles to restrict response data<br/>
We can use user roles to restrict the data that is returned in the API responses. By associating specific roles with users, we can control the access level of each user and determine which data they are allowed to view or modify.<br/>
For Example:<br/>
- Admin Role: Full access to all data and API endpoints. ( Upload, Download );
- User Role: Limited access to specific data and API endpoints. ( Download );

### Use Caching to Improve Performance<br/>
  To DB We save the metadata of the file uploaded to MinIO.
  We can use caching to improve the performance of database queries by storing frequently accessed data in memory.<br/>
  For example:
  - User send request to upload/download file to MinIO, but He send the same of file and bucket name, we can check the cache to see if the file has already been uploaded/downloaded and check the valid of the pre-signed URL.<br/>
  - If pre-signed URL is valid, we can return the pre-signed URL from the cache without querying the database, reducing the response time and database load.<br/>
  - To cache we can save pre-signed URL and metadata of the file uploaded to MinIO.<br/>
  - We can use a caching mechanism like Redis to store the pre-signed URLs and metadata in memory for faster access.<br/>

## Design API
- API should have endpoints for initiating upload, uploading, and downloading binary data
- API should be secured with JWT
- API should have endpoints for user registration and authentication

### Authentication Endpoints:
```http request
POST /api/v3/auth/register
```
register is a optional api for the Test, we can use the default user and password to login.

```http request
POST /api/v3/auth/login
```

### Minio Storage Endpoints:

```http request
GET /api/v3/minio/initiate/upload/{fileName}/{bucketName}
```

```http request
GET /api/v3/minio/initiate/download/{fileName}/{bucketName}
```


## Documentation:
- You can access the Swagger Specification [here](storage-minio-swagger.yaml)
- You can use Swagger Editor to view the Swagger Specification [here](https://editor.swagger.io/), just copy and paste the content of the file [storage-minio-swagger.yaml](storage-minio-swagger.yaml) to the editor.

## Implement API
- You can find the implementation of the API in the [src](src) directory.