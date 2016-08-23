package com.example.integration;

import java.security.PublicKey;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class DummyAuthenticator implements PublickeyAuthenticator {

	@Override
	public boolean authenticate(String username, PublicKey key, ServerSession session) {
		// TODO Auto-generated method stub
		return true;
	}

}
