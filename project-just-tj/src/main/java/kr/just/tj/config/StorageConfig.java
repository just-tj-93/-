package kr.just.tj.config;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;

@Configuration
public class StorageConfig {
	
    @Value("${oracle.oci.user}")
    private String user;
    
    @Value("${oracle.oci.tenancy}")
    private String tenancy;
    
    @Value("${oracle.oci.fingerprint}")
    private String fingerprint;
    
    @Value("${oracle.oci.namespace}")
    private String namespace;
    
    @Value("${oracle.oci.region}")
    private String region;
	
    @Value("${oracle.oci.bucket-name}")
    private String bucketName;
    
    @Value("${oracle.oci.privateKeyPath}")
    private String privateKeyPath;
    
	@Bean
    public ObjectStorageClient objectStorageClient() throws Exception {
		
		AuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
	            .userId(user)
	            .tenantId(tenancy)
	            .fingerprint(fingerprint)
	            .privateKeySupplier(new SimplePrivateKeySupplier(privateKeyPath))
	            .region(Region.valueOf(region))
	            .build();
		
	        return ObjectStorageClient.builder().build(provider);
    }

    public void uploadFile(ObjectStorageClient client) throws Exception {
    	
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName("ex1")
                .putObjectBody(new FileInputStream("path/to/example.txt"))
                .build();

        PutObjectResponse putObjectResponse = client.putObject(putObjectRequest);
        System.out.println("File uploaded to: " + putObjectResponse);
    }
}

