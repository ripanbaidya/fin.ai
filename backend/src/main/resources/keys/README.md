## RSA Key Pair

For local development, generate the keys in `backend/src/main/resources/keys/`:

Create the required folders:

```bash
mkdir keys
cd keys
```

Make sure **OpenSSL** is installed on your machine.

1. Generate the RSA private key:

   ```bash
   openssl genrsa -out private_key.pem 2048
   ```

2. Generate the public key from the private key:

   ```bash
   openssl rsa -in private_key.pem -pubout -out public_key.pem
   ```

After running the commands, the `keys` folder will contain:

```text
|-- keys/
    |-- private_key.pem
    |-- public_key.pem
```

Keep `private_key.pem` out of version control. The repository ignores this local development key.

In production, mount the private and public keys as secrets outside the application artifact and configure
`JWT_PRIVATE_KEY_PATH` and `JWT_PUBLIC_KEY_PATH` with their `file:` resource locations, for example
`file:/run/secrets/jwt_private.pem`. The `public_key.pem` file can be shared with services that need to
verify tokens generated using the private key.
