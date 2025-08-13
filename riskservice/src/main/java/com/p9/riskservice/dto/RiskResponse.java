package com.p9.riskservice.dto;

/**
 * DTO représentant la réponse d’évaluation du risque de diabète.
 * Contient l’ID du patient, le niveau de risque et le nombre de déclencheurs trouvés.
 */
public class RiskResponse {

    // Identifiant unique du patient
    private Long patientId;

    // Niveau de risque : None | Borderline | In Danger | Early onset
    private String risk;

    // Nombre de déclencheurs identifiés dans les notes
    private int triggerCount;

    // Constructeur par défaut
    public RiskResponse() {}

    /**
     * Constructeur avec paramètres pour initialiser tous les champs.
     *
     * @param patientId identifiant du patient
     * @param risk niveau de risque
     * @param triggerCount nombre de déclencheurs détectés
     */
    public RiskResponse(Long patientId, String risk, int triggerCount) {
        this.patientId = patientId;
        this.risk = risk;
        this.triggerCount = triggerCount;
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }

    public int getTriggerCount() { return triggerCount; }
    public void setTriggerCount(int triggerCount) { this.triggerCount = triggerCount; }
}
