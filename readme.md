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
AuthorizedKeysCommand /usr/local/bin/gitakc
```
It will automatically download and cache(for `ttl` seconds) user's public keys, and authorize for user.

### Usage
If you love JVM, use script below:
```bash
# Compile with JVM
mill -i "gitakc.jvm[3.0.0].assembly"
# Copy to local binary path
cp out/gitakc/jvm/3.0.0/assembly.dest/out.jar /usr/local/bin/gitakc
# Compile with Native
mill -i "gitakc.native[2.13.8].nativeLink"
# Copy to local binary path
cp out/gitakc/native/2.13.8/nativeLink.dest/out /usr/local/bin/gitakc
# Copy example(and edit)
cp gitakc/resources/test.json /etc/gitakc.json
vim /etc/gitakc.json
# Restart sshd
systemctl restat sshd
```
Scala Native is suggestted via noticeable performance improvement:
```
[sequencer@manectric gitakc]$ time sudo /usr/local/bin/gitakc sequencer
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDEKj3mEhnggrAI8pbTGhv80fkjYsvsSWhpBVUyB1ITJw/o1BAxHU/ZvOs8TxEGEDS9Mhy7ekKBOhSFrQ4jPO88ebELBQSNNzsTgPAlRE+sDBm6o79FWntfLO+j8NZ05QSg8cXNIv6/vGdzamAyCMW+8FkatEiepelqvWf2y0RAJJmW5Ci/etPPJm5jqt1mPtCAsrWRZa+ZO8EwCCIbi8qh2WluXkMMy09NUUYHtKOv25hfpLywmuodFcJOCb4buqoHVlgCZT0amQSl3KombVGR9ysr2LzGQ08fN/ZzaMNIS3ZaXYbisMz7hWIINgfXyM8SdKUbo6xPNH1tb2IDVcPvSQj3I3pnNPOcrQtPqv951+mEq1JkAw/x1QIQq3PwG3edliTx61UWOeOsnxDJ61GgeNlHcrM+Lp690bkfXp5qZCLEvw3miwCBaPcmCONpiKpZEjeQz/Rc0O1hijBhfPSb+xRvmh5JU84tqVFV2XNxt81901tOL/OuxF1Sp+iHnxkEad2erLtNO8P0JkZllZsB7fh7aRHnU+urtFNReqMIXKEKAb8AGk9J8jf/BcWrNdrnuDvKIoi2hrk8gVhvACiH6zWBCaVq9BuLLW5li0yooGieW1vMD/pZ3rsaJRP5p5641DCSCtHJ6TU0tah/XxL3Z7TmTD3SJhAg4rpufacS9Q==
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAILKhZ9/kr76CbYOEGAt2WiiLriXxZlUizEFVH37uXEgL
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFirv4baCDyYyOlaoGFfEUbbRo1l+ktZ55o8JzttyDqC
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIEVi/XVTQ0mms9sDmJK+8ipO/H/moa+6CkC0Xa/ibaIX
ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBHS+BfCLZm0sAE5VMrtACa28cN2esiZJGbnBRXBhQ9/tRB9k+0T0X/yxVPjHrMbUZL56ouLyZ0ZzbcOi1KeDH2o=


real    0m0.223s
user    0m0.330s
sys     0m0.028s
[sequencer@manectric gitakc]$ time sudo out/gitakc/native/2.13.8/nativeLink.dest/out sequencer
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDEKj3mEhnggrAI8pbTGhv80fkjYsvsSWhpBVUyB1ITJw/o1BAxHU/ZvOs8TxEGEDS9Mhy7ekKBOhSFrQ4jPO88ebELBQSNNzsTgPAlRE+sDBm6o79FWntfLO+j8NZ05QSg8cXNIv6/vGdzamAyCMW+8FkatEiepelqvWf2y0RAJJmW5Ci/etPPJm5jqt1mPtCAsrWRZa+ZO8EwCCIbi8qh2WluXkMMy09NUUYHtKOv25hfpLywmuodFcJOCb4buqoHVlgCZT0amQSl3KombVGR9ysr2LzGQ08fN/ZzaMNIS3ZaXYbisMz7hWIINgfXyM8SdKUbo6xPNH1tb2IDVcPvSQj3I3pnNPOcrQtPqv951+mEq1JkAw/x1QIQq3PwG3edliTx61UWOeOsnxDJ61GgeNlHcrM+Lp690bkfXp5qZCLEvw3miwCBaPcmCONpiKpZEjeQz/Rc0O1hijBhfPSb+xRvmh5JU84tqVFV2XNxt81901tOL/OuxF1Sp+iHnxkEad2erLtNO8P0JkZllZsB7fh7aRHnU+urtFNReqMIXKEKAb8AGk9J8jf/BcWrNdrnuDvKIoi2hrk8gVhvACiH6zWBCaVq9BuLLW5li0yooGieW1vMD/pZ3rsaJRP5p5641DCSCtHJ6TU0tah/XxL3Z7TmTD3SJhAg4rpufacS9Q==
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAILKhZ9/kr76CbYOEGAt2WiiLriXxZlUizEFVH37uXEgL
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFirv4baCDyYyOlaoGFfEUbbRo1l+ktZ55o8JzttyDqC
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIEVi/XVTQ0mms9sDmJK+8ipO/H/moa+6CkC0Xa/ibaIX
ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBHS+BfCLZm0sAE5VMrtACa28cN2esiZJGbnBRXBhQ9/tRB9k+0T0X/yxVPjHrMbUZL56ouLyZ0ZzbcOi1KeDH2o=


real    0m0.010s
user    0m0.007s
sys     0m0.003s
```
