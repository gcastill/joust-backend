package com.joust.be.web.controller;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.joust.be.model.domain.Heartbeat;

@Component
@Path("/heartbeat")
public class HeartbeatController {

	@Resource
	private String buildVersion;

	@Resource
	private String buildLabel;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHeartbeat() {

		Heartbeat hb = new Heartbeat();
		hb.setMessage("I'm here");
		hb.setVersion(buildVersion);
		hb.setBuild(buildLabel);

		return Response.ok(hb).build();
	}

	public String getBuildLabel() {
		return buildLabel;
	}

	public void setBuildLabel(String buildLabel) {
		this.buildLabel = buildLabel;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
}
