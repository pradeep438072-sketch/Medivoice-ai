package com.example.suggestion

data class SymptomMedicineSuggestion(
    val symptom: String,
    val iconName: String,
    val recommendedOtcs: List<OtcRecommendation>,
    val homeCareTips: List<String>,
    val whenToSeeDoctor: String,
    val precautions: String
)

data class OtcRecommendation(
    val medicineName: String,
    val category: String,
    val standardDosage: String,
    val defaultFrequency: String,
    val suggestedTime: String,
    val instructions: String,
    val warning: String
)

object SymptomKnowledgeBase {
    val symptoms: List<SymptomMedicineSuggestion> = listOf(
        SymptomMedicineSuggestion(
            symptom = "Headache & Migraine",
            iconName = "headache",
            recommendedOtcs = listOf(
                OtcRecommendation(
                    medicineName = "Paracetamol",
                    category = "Pain Relief",
                    standardDosage = "500 mg",
                    defaultFrequency = "As Needed (max 3/day)",
                    suggestedTime = "02:00 PM",
                    instructions = "Take with water after a snack. Avoid alcohol.",
                    warning = "Do not exceed 3,000 mg in 24 hours to prevent liver toxicity."
                ),
                OtcRecommendation(
                    medicineName = "Ibuprofen",
                    category = "Pain Relief",
                    standardDosage = "200 mg",
                    defaultFrequency = "Every 6-8 Hours",
                    suggestedTime = "02:30 PM",
                    instructions = "Always take with food or milk to avoid gastric irritation.",
                    warning = "Avoid if you have stomach ulcers, kidney issues, or are pregnant."
                )
            ),
            homeCareTips = listOf(
                "Rest in a quiet, darkened room",
                "Drink 500 ml of cool water (dehydration is a frequent cause)",
                "Apply cold pack to forehead or warm compress to back of neck"
            ),
            whenToSeeDoctor = "Seek immediate emergency care if the headache is sudden and explosively severe ('thunderclap'), accompanied by confusion, stiff neck, or loss of vision.",
            precautions = "Do not take multiple medications containing acetaminophen/paracetamol simultaneously."
        ),
        SymptomMedicineSuggestion(
            symptom = "Fever & Chills",
            iconName = "fever",
            recommendedOtcs = listOf(
                OtcRecommendation(
                    medicineName = "Paracetamol",
                    category = "Pain Relief",
                    standardDosage = "650 mg",
                    defaultFrequency = "Every 6 Hours",
                    suggestedTime = "01:00 PM",
                    instructions = "Take with fluids. Rest immediately after.",
                    warning = "Maintain at least 4 to 6 hours between doses."
                )
            ),
            homeCareTips = listOf(
                "Drink abundant fluids: water, coconut water, or electrolyte solution",
                "Wear light, breathable cotton clothes",
                "Use lukewarm water sponge bath if temperature exceeds 101°F (38.3°C)"
            ),
            whenToSeeDoctor = "Consult a physician immediately if fever exceeds 103°F (39.4°C), lasts more than 3 consecutive days, or is accompanied by difficulty breathing.",
            precautions = "Never give aspirin to children or teenagers recovering from viral infections."
        ),
        SymptomMedicineSuggestion(
            symptom = "Acidity, Heartburn & GERD",
            iconName = "stomach",
            recommendedOtcs = listOf(
                OtcRecommendation(
                    medicineName = "Antacid Chewable Tablet",
                    category = "Gastrointestinal",
                    standardDosage = "1-2 Chewable Tabs",
                    defaultFrequency = "After Meals / As Needed",
                    suggestedTime = "02:00 PM",
                    instructions = "Chew thoroughly before swallowing.",
                    warning = "May interact with absorption of iron and other medications. Space by 2 hours."
                ),
                OtcRecommendation(
                    medicineName = "Pantoprazole",
                    category = "Gastrointestinal",
                    standardDosage = "40 mg",
                    defaultFrequency = "Once Daily (Morning)",
                    suggestedTime = "07:30 AM",
                    instructions = "Take 30 minutes before breakfast on an empty stomach.",
                    warning = "Intended for short-term course; consult a doctor if symptoms persist."
                )
            ),
            homeCareTips = listOf(
                "Avoid spicy, greasy, citrus, and caffeinated foods/drinks",
                "Do not lie down for at least 2 to 3 hours after a meal",
                "Elevate the head of your bed by 6 inches"
            ),
            whenToSeeDoctor = "Seek urgent care if heartburn feels like crushing chest pressure radiating to arm or jaw, or causes difficulty swallowing.",
            precautions = "Antacids can mask symptoms of more serious underlying cardiac or gastrointestinal conditions."
        ),
        SymptomMedicineSuggestion(
            symptom = "Cough, Cold & Congestion",
            iconName = "cold",
            recommendedOtcs = listOf(
                OtcRecommendation(
                    medicineName = "Dextromethorphan Syrup",
                    category = "Respiratory",
                    standardDosage = "10 ml",
                    defaultFrequency = "Every 8 Hours",
                    suggestedTime = "08:00 PM",
                    instructions = "Use provided measuring cup. Take after food.",
                    warning = "Do not take with MAOI antidepressants. May cause mild drowsiness."
                ),
                OtcRecommendation(
                    medicineName = "Saline Nasal Spray",
                    category = "Respiratory",
                    standardDosage = "2 Sprays each nostril",
                    defaultFrequency = "3 Times Daily",
                    suggestedTime = "09:00 AM",
                    instructions = "Clear nasal passage gently before spraying.",
                    warning = "Safe, non-medicated saline solution suitable for frequent use."
                )
            ),
            homeCareTips = listOf(
                "Perform warm steam inhalation for 5-10 minutes twice daily",
                "Drink warm honey-lemon tea (honey is clinically proven for throat coating)",
                "Use a clean room humidifier while sleeping"
            ),
            whenToSeeDoctor = "See a doctor if cough lasts longer than 2 weeks, produces blood-tinged phlegm, or causes wheezing and chest pain.",
            precautions = "Honey is not safe for infants under 1 year of age."
        ),
        SymptomMedicineSuggestion(
            symptom = "Allergies & Sneezing",
            iconName = "allergy",
            recommendedOtcs = listOf(
                OtcRecommendation(
                    medicineName = "Cetirizine",
                    category = "Allergy",
                    standardDosage = "10 mg",
                    defaultFrequency = "Once Daily",
                    suggestedTime = "09:00 PM",
                    instructions = "Take in the evening with water.",
                    warning = "May cause mild drowsiness in some individuals. Avoid driving if affected."
                ),
                OtcRecommendation(
                    medicineName = "Loratadine",
                    category = "Allergy",
                    standardDosage = "10 mg",
                    defaultFrequency = "Once Daily",
                    suggestedTime = "08:00 AM",
                    instructions = "Non-drowsy formulation. Take with or without food.",
                    warning = "Consult doctor if you have severe kidney or liver impairment."
                )
            ),
            homeCareTips = listOf(
                "Keep windows closed during high pollen count hours",
                "Wash face and rinse eyes with cold water after outdoor exposure",
                "Change clothes immediately upon returning home"
            ),
            whenToSeeDoctor = "Call emergency services immediately if experiencing throat swelling, facial lip swelling, or breathing difficulty (anaphylaxis signs).",
            precautions = "Do not combine multiple antihistamine drugs without pharmacist clearance."
        ),
        SymptomMedicineSuggestion(
            symptom = "Sore Throat & Hoarseness",
            iconName = "throat",
            recommendedOtcs = listOf(
                OtcRecommendation(
                    medicineName = "Antiseptic Throat Lozenges",
                    category = "Oral Care",
                    standardDosage = "1 Lozenge",
                    defaultFrequency = "Every 3-4 Hours",
                    suggestedTime = "11:00 AM",
                    instructions = "Dissolve slowly in the mouth. Do not bite or swallow whole.",
                    warning = "Avoid giving to young children due to choking risk."
                )
            ),
            homeCareTips = listOf(
                "Gargle with warm salt water (1/2 teaspoon salt in 1 glass of warm water) 3 times daily",
                "Rest voice and avoid whispering which strains vocal cords",
                "Sip warm broth or ginger water frequently"
            ),
            whenToSeeDoctor = "Seek medical evaluation if severe pain impairs swallowing saliva, or if white patches appear on tonsils (possible bacterial strep throat).",
            precautions = "Throat lozenges only soothe irritation; they do not cure bacterial infections."
        )
    )
}
