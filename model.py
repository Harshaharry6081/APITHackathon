# model.py - Enhanced AI-Powered Predictive Analytics for Student Dropout Prevention
import pandas as pd
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score
import json

# Enhanced synthetic dataset with multiple risk factors
# Factors: attendance, exam_score, socio_economic_status (1-5), transport_allowance_used (0/1),
# migration_indicator (0/1), gender (0=Male, 1=Female), social_category (0=General, 1=ST/SC)
data = {
    "student_id": ["ST001", "ST002", "ST003", "ST004", "ST005", "ST006", "ST007", "ST008", "ST009", "ST010",
                   "ST011", "ST012", "ST013", "ST014", "ST015", "ST016", "ST017", "ST018", "ST019", "ST020"],
    "attendance": [0.45, 0.92, 0.38, 0.85, 0.50, 0.95, 0.42, 0.88, 0.35, 0.90,
                   0.55, 0.80, 0.40, 0.93, 0.48, 0.87, 0.33, 0.91, 0.46, 0.84],
    "exam_score": [35, 78, 28, 72, 42, 85, 30, 75, 25, 82,
                   45, 68, 32, 88, 38, 73, 27, 80, 40, 70],
    "socio_economic_status": [1, 4, 1, 4, 2, 5, 1, 4, 1, 5,
                              2, 3, 1, 5, 2, 4, 1, 4, 2, 3],  # 1=Low, 5=High
    "transport_allowance_used": [0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
                                 1, 1, 0, 1, 0, 1, 0, 1, 1, 1],
    "migration_indicator": [1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
                           0, 0, 1, 0, 1, 0, 1, 0, 0, 0],  # 1=Migrant family
    "gender": [1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
               1, 0, 1, 0, 1, 0, 1, 0, 1, 0],  # 1=Female
    "social_category": [1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
                        0, 0, 1, 0, 1, 0, 1, 0, 0, 0],  # 1=ST/SC
    "dropout": [1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
                0, 0, 1, 0, 1, 0, 1, 0, 0, 0]  # 1=Dropout, 0=Continued
}

df = pd.DataFrame(data)

# Feature columns
feature_cols = ["attendance", "exam_score", "socio_economic_status", 
                "transport_allowance_used", "migration_indicator", "gender", "social_category"]

X = df[feature_cols]
y = df["dropout"]

# Train-test split for validation
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

# Train Logistic Regression model
model = LogisticRegression(random_state=42, max_iter=1000)
model.fit(X_train, y_train)

# Model Evaluation
y_pred = model.predict(X_test)
y_pred_proba = model.predict_proba(X_test)[:, 1]

print("\n=== AI-Powered Dropout Prevention Model ===")
print("\n📊 Model Performance Metrics:")
print(f"Accuracy: {accuracy_score(y_test, y_pred)*100:.2f}%")

# Confusion Matrix Analysis
cm = confusion_matrix(y_test, y_pred)
tn, fp, fn, tp = cm.ravel()

# PoC Success Criteria Calculation
inclusion_error = (fp / (tn + fp) * 100) if (tn + fp) > 0 else 0  # False positive rate
exclusion_error = (fn / (tp + fn) * 100) if (tp + fn) > 0 else 0  # False negative rate

print(f"\n✅ PoC Success Criteria:")
print(f"Inclusion Error (wrongly classified as at-risk): {inclusion_error:.2f}% (Target: <80%)")
print(f"Exclusion Error (missed dropouts): {exclusion_error:.2f}% (Target: <20%)")

print("\n📈 Confusion Matrix:")
print(f"True Negatives: {tn}, False Positives: {fp}")
print(f"False Negatives: {fn}, True Positives: {tp}")

print("\n📋 Classification Report:")
print(classification_report(y_test, y_pred, target_names=["Continued", "At-Risk"]))

# Feature Importance (Explainable AI)
print("\n🎯 Feature Importance (Explainable AI):")
feature_importance = pd.DataFrame({
    'feature': feature_cols,
    'coefficient': model.coef_[0],
    'abs_coefficient': np.abs(model.coef_[0])
}).sort_values('abs_coefficient', ascending=False)

for idx, row in feature_importance.iterrows():
    direction = "↑ increases" if row['coefficient'] > 0 else "↓ decreases"
    print(f"{row['feature']}: {direction} dropout risk (weight: {row['coefficient']:.3f})")

# Demo: Predict risk for high-risk student profile
print("\n\n=== Demo: High-Risk Student Analysis ===")
high_risk_student = pd.DataFrame({
    "attendance": [0.45],
    "exam_score": [35],
    "socio_economic_status": [1],  # Low SES
    "transport_allowance_used": [0],  # Not using transport
    "migration_indicator": [1],  # Migrant family
    "gender": [1],  # Female
    "social_category": [1]  # ST/SC
})

pred = model.predict(high_risk_student)[0]
proba = model.predict_proba(high_risk_student)[0][1]

print(f"Student Profile: Female, ST/SC, Migrant family, Low SES")
print(f"Attendance: 45%, Exam Score: 35, No transport allowance")
print(f"\nPredicted Dropout Risk: {'HIGH' if pred == 1 else 'LOW'}")
print(f"Risk Probability: {proba*100:.1f}%")

# Generate risk reasons for explainability
risk_reasons = []
if high_risk_student['attendance'].values[0] < 0.75:
    risk_reasons.append(f"Low attendance ({high_risk_student['attendance'].values[0]*100:.0f}%)")
if high_risk_student['exam_score'].values[0] < 50:
    risk_reasons.append(f"Below-average exam scores ({high_risk_student['exam_score'].values[0]:.0f})")
if high_risk_student['socio_economic_status'].values[0] <= 2:
    risk_reasons.append("Low socio-economic status")
if high_risk_student['transport_allowance_used'].values[0] == 0:
    risk_reasons.append("Transport allowance not utilized")
if high_risk_student['migration_indicator'].values[0] == 1:
    risk_reasons.append("Seasonal migrant family")
if high_risk_student['gender'].values[0] == 1:
    risk_reasons.append("Female student (higher dropout risk)")
if high_risk_student['social_category'].values[0] == 1:
    risk_reasons.append("Belongs to ST/SC category")

print(f"\nRisk Factors Identified:")
for reason in risk_reasons:
    print(f"  • {reason}")

# Save model results for API integration
results = {
    "model_accuracy": float(accuracy_score(y_test, y_pred)),
    "inclusion_error": float(inclusion_error),
    "exclusion_error": float(exclusion_error),
    "feature_importance": feature_importance.to_dict('records'),
    "poc_criteria_met": bool(inclusion_error < 80 and exclusion_error < 20)
}

with open('model_results.json', 'w') as f:
    json.dump(results, f, indent=2)

print("\n✅ Model results saved to model_results.json")
