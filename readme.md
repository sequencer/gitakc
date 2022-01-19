# GitAKC(Git AuthorizedKeysCommand)

This is a small program to use user's GitHub keys for ssh authorization.  
Only need a system user add his/her ssh public key to GitHub, and create a map file in `/etc/gitakc.json` in format below:
```json
{
  "ttl": "600",
  "userMap": {
    "sequencer": ["sequencer"]
  },
  "cacheFolder": "/tmp/gitakc"
}
```
Set your `ssh_config` to
```
AuthorizedKeysCommand gitakc
```
It will automatically download and cache(for `ttl` seconds) user's public keys, and authorize for user.

### Usage
```
# Compile
mill -i "gitakc.jvm[3.0.0].assembly"
# Copy to local binary path
p out/gitakc/jvm/3.0.0/assembly.dest/out.jar /usr/local/bin/gitakc
# Copy example(and edit)
cp gitakc/resources/test.json /etc/gitakc.json
vim /etc/gitakc.json
# Restart sshd
systemctl restat sshd
```

### NOTICE
Scala Native is not work since scala-native/scala-native#2135 and will be fixed at scala-native/scala-native#2141
