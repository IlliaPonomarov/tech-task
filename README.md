
# TASK
## Content
 - [Understand the Requirements](#understand-the-requirements)
 - [Identify Issues](#identify-issues)
 - [Design API](#design-api)
 - [Documentation](#documentation)
 - [Implement API](#implement-api)
 - [How to run the project](#how-to-run-the-project)
 - [Docker and Docker-compose](#docker-and-docker-compose)

## Understand the Requirements:
We should design an API that allows integrators to upload and download binary data to and from MinIO.
### Data Organization:
 Data should be stored in multiple buckets based on data type (fingerprints, faces,
attachments, videos).
### API Functionalities:
1. Initiate Upload:
- Give the pre-signed URL for uploading binary data to MinIO.
- The pre-signed URL should be valid for a limited time.
- The pre-signed URL should be unique for each upload request.
- The pre-signed URL should be associated with the user, file and bucket name.
- For Users with User Role or Admin role , can get different response.<br/>

**Request:**
```http request
GET /api/v3/minio/initiate/upload/{fileName}/{attachmentType}
```

**Response for User Role:**
```json
{
  "preSignedUrl": "https://minio.example.com/upload/1234567890",
  "urlType": "upload",
  "expiresAt": "2022-12-31T23:59:59Z",
  "metadata": {
        "fileName": "fingerprint_1234567890"
    }
}
```

**Response for Admin Role:**
```json
{
  "preSignedUrl": "https://minio.example.com/upload/1234567890",
  "urlType": "upload",
  "expiresAt": "2022-12-31T23:59:59Z",
  "metadata": {
    "fileId": "1234567890",
    "bucketName": "fingerprints",
    "fileName": "fingerprint_1234567890",
    "createdAt": "2022-12-31T23:59:59Z",
    "updatedAt": "2022-12-31T23:59:59Z"
  }
}
```

2. Initiate Download:
- Give the pre-signed URL for downloading binary data from MinIO.
- The pre-signed URL should be valid for a limited time.
- The pre-signed URL should be unique for each download request and should be associated with the user, file and bucket name.
- If the user is authorized to download the file, the pre-signed URL should be returned.
- For Users with User Role or Admin role , can get different response.<br/>

**Request:**
```http request
GET /api/v3/minio/initiate/download/{fileName}/{attachmentType}
```

**Response for User Role:**
```json
{
  "preSignedUrl": "https://minio.example.com/download/1234567890",
  "urlType": "download",
  "expiresAt": "2022-12-31T23:59:59Z",
  "metadata": {
    "fileName": "fingerprint_1234567890"
  }
}
```

**Response for Admin Role:**
```json
{
  "preSignedUrl": "https://minio.example.com/download/1234567890",
  "urlType": "download",
  "expiresAt": "2022-12-31T23:59:59Z",
  "metadata": {
    "fileId": "1234567890",
    "bucketName": "fingerprints",
    "fileName": "fingerprint_1234567890",
    "createdAt": "2022-12-31T23:59:59Z",
    "updatedAt": "2022-12-31T23:59:59Z"
  }
}
```


### Security:
- We should secure the API with JWT.
- We should have endpoints for user registration and authentication.
- Unauthorized users should not be able to access the API from the Minio Storage API.
- We should have a temporary bucket named temp for storing binary data.

## Identify Issues:

### Database Issues<br/>
For example , If we get a fingerprint binary file from the user, we should save the fingerprint file info to the database.
We can use One-toMany relationship between the user and the file to associate the file with the user.<br/>

#### How Can we associate the fingerprint file with the column from fingerprint table?
- We can use the processed_data_user_id column in the fingerprint table to associate the fingerprint file with the user who processed the data.
- We can use the uploaded_file_user_id column in the fingerprint table to associate the fingerprint file with the user who uploaded the file.
- If We wanna get info about user who uploaded and processed the file , we can just compare the user_id from the user table with the processed_data_user_id and uploaded_file_user_id from the fingerprint table.

```postgresql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
                
);

CREATE TABLE fingerprint (
    id SERIAL PRIMARY KEY,
    processed_data_user_id INTEGER REFERENCES users(id),  -- User who processed the fingerprint data
    uploaded_file_user_id INTEGER REFERENCES users(id), -- User who uploaded the fingerprint file
    fingerprint_name VARCHAR(255) NOT NULL, -- Name of the fingerprint file
    captured_date TIMESTAMP NOT NULL, -- Date when the fingerprint was captured
    fingerprint_quality VARCHAR(50) NOT NULL, -- Quality of the fingerprint data
    data_quality VARCHAR(50) NOT NULL, -- Quality of the data
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP-- Creation timestamp
);

CREATE TABLE face (
    id SERIAL PRIMARY KEY,
    processed_data_user_id INTEGER REFERENCES users(id),  -- User who processed the fingerprint data
    uploaded_file_user_id INTEGER REFERENCES users(id), -- User who uploaded the fingerprint file
    face_name VARCHAR(255) NOT NULL, -- Name of the face file
    captured_date TIMESTAMP NOT NULL, -- Date when the face was captured
    face_position VARCHAR(50) NOT NULL, -- Position of the face in the image
    data_quality VARCHAR(50) NOT NULL, -- Quality of the face data
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Last updated timestamp
);

CREATE TABLE attachment (
    id SERIAL PRIMARY KEY,
    processed_data_user_id INTEGER REFERENCES users(id),  -- User who processed the fingerprint data
    uploaded_file_user_id INTEGER REFERENCES users(id), -- User who uploaded the fingerprint file
    attachment_name VARCHAR(255) NOT NULL, -- Name of the attachment file
    attachment_type VARCHAR(50) NOT NULL, -- Type of the attachment 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Last updated timestamp
);

CREATE TABLE video (
    id SERIAL PRIMARY KEY,
    processed_data_user_id INTEGER REFERENCES users(id),  -- User who processed the fingerprint data
    uploaded_file_user_id INTEGER REFERENCES users(id), -- User who uploaded the fingerprint file
    video_name VARCHAR(255) NOT NULL, -- Name of the video file
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Last updated timestamp
);

CREATE TABLE files_minio (
    id SERIAL PRIMARY KEY,
    uploaded_user_id INTEGER REFERENCES users(id), -- User who uploaded the file
    file_name VARCHAR(255) NOT NULL, -- Name of the file
    fingerprint_id INTEGER REFERENCES fingerprint(id), -- Fingerprint ID associated with the file
    face_id INTEGER REFERENCES face(id), -- Face ID associated with the file
    attachment_id INTEGER REFERENCES attachment(id), -- Attachment ID associated with the file
    video_id INTEGER REFERENCES video(id), -- Video ID associated with the file
    bucket_name VARCHAR(255) NOT NULL, -- Name of the bucket
    presigned_url VARCHAR(255) NOT NULL, -- Pre-signed URL for file upload/download
    presigned_url_type VARCHAR(50) NOT NULL, -- Type of pre-signed URL (upload/download)
    url_expires_at TIMESTAMP NOT NULL, -- Expiration time for the pre-signed URL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Last updated timestamp
);
```

### Authorization and Authentication<br/>
- We should secure the API with **JWT** to authenticate users and authorize access to the API endpoints.
- We should separate the user roles and permissions to control access to the API endpoints and control responses based on the user role.

If you wanna test the API, you can register a new user and login to get the JWT token.
```http request
POST /api/v3/auth/register
```

Request Body:
```json
{
  "username": "testuser",
  "password": "testpassword"
}
```
```http request
POST /api/v3/auth/login
```


Request Body:
```json
{
  "username": "testuser",
  "password": "testpassword"
}
```

Response:
```json
{
"token" : "token",
"expiresAt": "2022-12-31T23:59:59Z"
}
```


### Optimize Database with Indexes<br/>
  We can optimize the database performance by creating indexes on the columns that are frequently used in queries. 
  By indexing the user ID.
  Use case:
    - When a user requests to download a file, we can quickly retrieve the file metadata by searching for the user ID and file name in the database.

```postgresql
CREATE INDEX user_id_index ON files_minio(user_id);
```


### Use Proxy to restrict number of requests to the our API<br/>
  We can use a proxy server to restrict the number of requests to the API. By limiting the number of requests per user, we can prevent abuse and ensure fair usage of the API resources.<br/>
  For example:
  - We can set a rate limit on the number of requests per user per minute.
  - If a user exceeds the rate limit, the proxy server can return an error response or block the user from making further requests.
  - We can use a proxy server like NGINX or HAProxy to implement rate limiting and protect the API from abuse.

```nginx
http {
    limit_req_zone $binary_remote_addr zone=mylimit:10m rate=10r/s;

    server {
        location /api/v3/minio/initiate/upload {
            limit_req zone=mylimit burst=5 nodelay;
            proxy_pass http://minio.example.com;
        },
        location /api/v3/minio/initiate/download {
            limit_req zone=mylimit burst=5 nodelay;
            proxy_pass http://minio.example.com;
        }
    }
}
```



### Use User roles to restrict response data<br/>
We can use user roles to restrict the response data based on the user's role. <br/>
For example, we can return different response data for users with different roles, such as User and Admin.<br/>
See [API Functionalities](#api-functionalities) for more details.

### Use Caching to Improve Performance<br/>
  To DB We save the metadata of the file uploaded to MinIO.
  We can use caching to improve the performance of database queries by storing frequently accessed data in memory.<br/>
  Use case:
  - User send request to upload/download file to MinIO, but He send the same of file and bucket name, we can check the cache to see if the file has already been uploaded/downloaded and check the valid of the pre-signed URL.<br/>
  - If pre-signed URL is valid, we can return the pre-signed URL from the cache without querying the database, reducing the response time and database load.<br/>
  - To cache we can save pre-signed URL and metadata of the file uploaded to MinIO.<br/>
  - We can use a caching mechanism like Redis to store the pre-signed URLs and metadata in memory for faster access.<br/>

#### How can we determine at cache that pre-signed URL was expired or not?
- We can use the expiration time of the pre-signed URL to determine if the URL has expired.
- When a user requests to upload/download a file, we can check the expiration time of the pre-signed URL in the cache.
- If the pre-signed URL has expired, we can remove the URL from the cache and generate a new pre-signed URL for the user.
- We can periodically check the expiration time of the pre-signed URLs in the cache and update the cache with new pre-signed URLs as needed.

If you wanna clean up you cache from the expired pre-signed URL, you can use this bash script on the server, where you have Redis running:
```bash
#!/bin/bash
cleanup_expired_urls() {
    # Get the current timestamp
    current_timestamp=$(date +%s)

    # Get all user keys from Redis cache
    user_keys=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT -a $REDIS_PASSWORD keys "$CACHE_KEY_PREFIX*")

    # Take each user key and check for expired URLs
    for key in $user_keys; do
        # Get all cache objects for the user key
        cache_objects=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT -a $REDIS_PASSWORD hgetall "$key")

        # Get all preSignedUrl objects for the user key
        while IFS= read -r line; do
            # Check if the line is a preSignedUrl object
            if [[ "$line" =~ ^preSignedUrl:[0-9]+$ ]]; then
                # get cache object by key
                cache_object=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT -a $REDIS_PASSWORD hget "$key" "$line")

                # parse JSON 
                expires_at=$(echo "$cache_object" | jq -r '.expiresAt // empty')

                # If the expiresAt field is not empty and the expiration time is less than the current timestamp, delete the expired object
                if [[ ! -z "$expires_at" && $(date -d "$expires_at" +%s) -lt "$current_timestamp" ]]; then
                    echo "Removing expired pre-signed URL: $line"
                    redis-cli -h $REDIS_HOST -p $REDIS_PORT -a $REDIS_PASSWORD hdel "$key" "$line"
                fi
            fi
        done <<< "$cache_objects"
    done

    echo "Cleanup completed successfully"
}

# Call the cleanup_expired_urls function
cleanup_expired_urls
```
- Set up task scheduler to run the script periodically to clean up the cache from expired pre-signed URLs.
- We should set up a cron job to run the cleanup_expired_urls.sh script every hour to remove expired pre-signed URLs from the cache.
- cron job is a time-based job scheduler in Unix-like operating systems. It can be used to schedule tasks to run periodically at fixed intervals.
```bash
# Run the cleanup_expired_urls.sh script every hour
0 * * * * /path/to/cleanup_expired_urls.sh
```


## Minio file LifeCycle Configuration
- We can use Minio's file life cycle configuration to automatically delete files after a certain period of time. This feature can help manage storage costs and ensure data privacy by removing outdated or unused files from the storage system.<br/>

## Design API
- You can access the Swagger Specification [here](storage-minio-swagger.yaml) to view the API design.

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
GET /api/v3/minio/initiate/upload/{fileName}/{attachmentType}
```

```http request
GET /api/v3/minio/initiate/download/{fileName}/{attachmentType}
```

### Remove old object from Minio
- We can use Minio's file life cycle configuration to automatically delete files after a certain period of time. This feature can help
manage storage costs and ensure data privacy by removing outdated or unused files from the storage system.<br/>

File Example:

- temp-lifecycle.json 
```json
{
  "Rules": [{
      "Expiration": {
        "Date": "2020-01-01T00:00:00.000Z"
      },
      "ID": "OldPictures",
      "Filter": {
        "Prefix": "old/"
      },
      "Status": "Enabled"
    },
    {
      "Expiration": {
        "Days": 7
      },
      "ID": "TempUploads",
      "Filter": {
        "Prefix": "temp/"
      },
      "Status": "Enabled"
    }
  ]
}
```

- lifecycle-all-buckets.json
```json
{
  "Rules": [{
      "ID": "BucketRule",
      "Status": "Enabled"
    }
  ]
}
```


```bash
mc alias set myminio http://minio.example.com accesskey secretkey
mc ilm import myminio/temp temp-lifecycle.json 

# create fingerprint, faces, attachments, videos buckets
mc mb myminio/fingerprints
mc mb myminio/faces
mc mb myminio/attachments
mc mb myminio/videos

mc ilm import myminio/fingerprints lifecycle-all-buckets.json
mc ilm import myminio/faces lifecycle-all-buckets.json
mc ilm import myminio/attachments lifecycle-all-buckets.json
mc ilm import myminio/videos lifecycle-all-buckets.json
```

# Documentation:
- You can access the Swagger Specification [here](storage-minio-swagger.yaml)
- You can use Swagger Editor to view the Swagger Specification [here](https://editor.swagger.io/), just copy and paste the content of the file [storage-minio-swagger.yaml](storage-minio-swagger.yaml) to the editor.

# Implement API
- You can find the implementation of the API in the [controllers](src/main/java/org/storage/biometrics/storagemimoio/storage/controllers) directory.

# How to run the project
- You can run the project using the following steps:
- Use docker-compose to run the project:
```bash
git clone https://github.com/IlliaPonomarov/tech-task.git
cd tech-task
mvn clean package
docker-compose up
```

# Docker and Docker-compose
- You can find the Dockerfile in the [Dockerfile](Dockerfile) file.
- You can find the docker-compose file in the [docker-compose.yaml](docker-compose.yaml) file.