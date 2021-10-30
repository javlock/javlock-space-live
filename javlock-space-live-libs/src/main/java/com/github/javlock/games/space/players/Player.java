package com.github.javlock.games.space.players;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

import com.github.javlock.utils.RSAKeyPairGenerator;

import lombok.Getter;
import lombok.Setter;

public class Player implements Serializable {
	private static final long serialVersionUID = -4157823777019497372L;

	private @Getter @Setter String uid = UUID.randomUUID().toString();
	private @Getter @Setter String userName = "USER:" + System.currentTimeMillis();

	private transient @Getter @Setter String privKey;
	private @Getter @Setter String publicKey;

	public void genNew() throws NoSuchAlgorithmException {
		RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
		PublicKey pub = keyPairGenerator.getPublicKey();
		PrivateKey priv = keyPairGenerator.getPrivateKey();
		setPrivKey(Base64.getEncoder().encodeToString(pub.getEncoded()));
		setPublicKey(Base64.getEncoder().encodeToString(priv.getEncoded()));
	}
}
