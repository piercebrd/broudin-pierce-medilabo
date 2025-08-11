package com.p9.riskservice.dto;

public class RiskResponse {
    private Long patientId;
    private String risk;       // None | Borderline | In Danger | Early onset
    private int triggerCount;

    public RiskResponse() {}
    public RiskResponse(Long patientId, String risk, int triggerCount) {
        this.patientId = patientId; this.risk = risk; this.triggerCount = triggerCount;
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }

    public int getTriggerCount() { return triggerCount; }
    public void setTriggerCount(int triggerCount) { this.triggerCount = triggerCount; }
}
