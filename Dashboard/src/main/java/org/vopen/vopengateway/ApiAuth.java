package org.vopen.vopengateway;

/**
 * Created by giovanni on 10/31/16.
 */

public class ApiAuth
{
    private String mPublicKey;
    private String mPrivateKey;

    public ApiAuth(String publicKey, String privateKey)
    {
        mPublicKey = publicKey;
        mPrivateKey = privateKey;
    }

    public static ApiAuth fromString(String completeString) throws MalformedApiKeyException
    {
        String str[] = completeString.split(":");
        if (str.length != 2) throw new MalformedApiKeyException();
        return new ApiAuth(str[0],str[1]);
    }

    @Override
    public String toString()
    {
        return mPublicKey + ":" + mPrivateKey;
    }

    public String getPublicKey() throws MalformedApiKeyException
    {
        if (mPublicKey == null) throw new MalformedApiKeyException();
        return mPublicKey;
    }

    public String getPrivateKey() throws MalformedApiKeyException
    {
        if (mPrivateKey == null) throw new MalformedApiKeyException();
        return mPrivateKey;
    }

}
