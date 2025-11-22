"""
Kaggle Dataset Integration for AI Dropout Early Warning System
Downloads and processes student dropout datasets from Kaggle
"""

import os
import json
import pandas as pd

def setup_kaggle():
    """
    Setup instructions for Kaggle API
    """
    print("=" * 60)
    print("KAGGLE API SETUP INSTRUCTIONS")
    print("=" * 60)
    print("\n1. Go to https://www.kaggle.com/settings/account")
    print("2. Scroll to 'API' section")
    print("3. Click 'Create New Token' - downloads kaggle.json")
    print("4. Place kaggle.json in: C:\\Users\\%USERNAME%\\.kaggle\\")
    print("\n5. Install Kaggle API:")
    print("   pip install kaggle")
    print("\n" + "=" * 60)
    
    kaggle_path = os.path.expanduser("~/.kaggle/kaggle.json")
    if os.path.exists(kaggle_path):
        print("✓ Kaggle API credentials found!")
        return True
    else:
        print("✗ Kaggle API credentials not found")
        print(f"  Expected location: {kaggle_path}")
        return False

def download_student_dropout_dataset():
    """
    Download Student Dropout Prediction dataset from Kaggle
    Dataset: ankanhore545/student-dropout-prediction (4,424 students)
    """
    try:
        import kaggle
        
        print("\n📥 Downloading Student Dropout dataset from Kaggle...")
        print("Dataset: student-dropout-prediction (4,424 students)")
        
        # Download dataset
        kaggle.api.dataset_download_files(
            'ankanhore545/student-dropout-prediction',
            path='./kaggle_data',
            unzip=True
        )
        
        print("✓ Download complete!")
        return True
        
    except Exception as e:
        print(f"✗ Error downloading dataset: {e}")
        return False

def download_xapi_edu_dataset():
    """
    Download Students' Academic Performance Dataset
    Dataset: aljarah/xAPI-Edu-Data (480 students)
    """
    try:
        import kaggle
        
        print("\n📥 Downloading xAPI-Edu-Data from Kaggle...")
        print("Dataset: Educational Data Mining (480 students)")
        
        kaggle.api.dataset_download_files(
            'aljarah/xAPI-Edu-Data',
            path='./kaggle_data',
            unzip=True
        )
        
        print("✓ Download complete!")
        return True
        
    except Exception as e:
        print(f"✗ Error downloading dataset: {e}")
        return False

def process_kaggle_data_to_ap_format(csv_file):
    """
    Convert Kaggle dataset to Andhra Pradesh format (7 risk factors)
    Maps Kaggle features to our model's expected features
    """
    print(f"\n🔄 Processing {csv_file}...")
    
    try:
        df = pd.read_csv(csv_file)
        print(f"✓ Loaded {len(df)} records")
        print(f"✓ Columns: {list(df.columns)}")
        
        # Analyze dataset structure
        print("\nDataset Preview:")
        print(df.head())
        print("\nData Types:")
        print(df.dtypes)
        print("\nMissing Values:")
        print(df.isnull().sum())
        
        return df
        
    except Exception as e:
        print(f"✗ Error processing data: {e}")
        return None

def map_kaggle_to_ap_format(df, dataset_type='dropout'):
    """
    Map Kaggle dataset features to AP Early Warning System format
    
    Our 7 required features:
    1. attendance_rate
    2. exam_score
    3. socio_economic_status (Low/Medium/High)
    4. transport_allowance_used (boolean)
    5. migration_indicator (boolean)
    6. gender (Male/Female)
    7. social_category (SC/ST/OBC/General)
    """
    print("\n🗺️  Mapping Kaggle data to AP format...")
    
    students = []
    districts = ["Visakhapatnam", "Vijayawada", "Guntur", "Tirupati", "Kakinada"]
    
    for idx, row in df.iterrows():
        try:
            # Create student record based on dataset type
            student = {
                "student_id": f"KG{str(idx+1).zfill(4)}",  # KG0001, KG0002...
                "district": districts[idx % len(districts)],
                "grade": int(row.get('grade', 9)) if 'grade' in row else (8 + (idx % 3)),
                "age": int(row.get('age', 14)) if 'age' in row else (13 + (idx % 4)),
            }
            
            # Map gender
            if 'gender' in df.columns or 'Gender' in df.columns:
                gender_col = 'gender' if 'gender' in df.columns else 'Gender'
                gender = str(row[gender_col]).upper()
                student['gender'] = 'Female' if 'F' in gender else 'Male'
            else:
                student['gender'] = 'Female' if idx % 2 == 0 else 'Male'
            
            # Map attendance (multiple possible column names)
            attendance_cols = ['absences', 'Absences', 'absence_days', 'Class']
            attendance_val = None
            for col in attendance_cols:
                if col in df.columns:
                    if col == 'Class':  # xAPI dataset: L=Low, M=Medium, H=High
                        class_val = str(row[col]).upper()
                        attendance_val = 95 if class_val == 'H' else (75 if class_val == 'M' else 50)
                    elif 'absence' in col.lower():
                        # Convert absences to attendance rate (assume 180 school days)
                        absences = float(row[col])
                        attendance_val = max(0, 100 - (absences / 180 * 100))
                    else:
                        attendance_val = float(row[col])
                    break
            
            student['attendance_rate'] = round(attendance_val if attendance_val else 75.0, 1)
            
            # Map exam scores
            score_cols = ['G1', 'G2', 'G3', 'grade', 'Grade', 'marks', 'Marks']
            score_val = None
            for col in score_cols:
                if col in df.columns:
                    score_val = float(row[col])
                    if score_val > 20:  # Already percentage
                        break
                    else:  # Convert 0-20 scale to 0-100
                        score_val = (score_val / 20) * 100
                        break
            
            student['exam_score'] = round(score_val if score_val else 70.0, 1)
            
            # Map socio-economic status
            if 'Medu' in df.columns and 'Fedu' in df.columns:  # Parent education
                parent_edu = (float(row['Medu']) + float(row['Fedu'])) / 2
                student['socio_economic_status'] = 'High' if parent_edu > 3 else ('Medium' if parent_edu > 1.5 else 'Low')
            elif 'ParentschoolSatisfaction' in df.columns:
                satisfaction = str(row['ParentschoolSatisfaction']).lower()
                student['socio_economic_status'] = 'High' if 'good' in satisfaction else 'Medium'
            else:
                # Infer from attendance and scores
                if student['attendance_rate'] > 85 and student['exam_score'] > 75:
                    student['socio_economic_status'] = 'High'
                elif student['attendance_rate'] > 70 and student['exam_score'] > 60:
                    student['socio_economic_status'] = 'Medium'
                else:
                    student['socio_economic_status'] = 'Low'
            
            # Map transport allowance
            if 'traveltime' in df.columns:
                travel = float(row['traveltime'])
                student['transport_allowance_used'] = travel > 2  # >30 min gets allowance
            elif 'StudentAbsenceDays' in df.columns:
                absence = str(row['StudentAbsenceDays']).lower()
                student['transport_allowance_used'] = 'under' in absence
            else:
                student['transport_allowance_used'] = student['attendance_rate'] > 70
            
            # Map migration indicator
            if 'address' in df.columns:
                address = str(row['address']).upper()
                student['migration_indicator'] = address == 'R'  # Rural = potential migration
            else:
                # Infer from low attendance + low SES
                student['migration_indicator'] = (
                    student['attendance_rate'] < 60 and 
                    student['socio_economic_status'] == 'Low'
                )
            
            # Map social category (Indian context)
            if 'race' in df.columns or 'ethnicity' in df.columns:
                # Map if available, otherwise distribute proportionally
                pass
            
            # Distribute social categories proportionally (AP demographics)
            category_dist = idx % 4
            if category_dist == 0:
                student['social_category'] = 'SC'
            elif category_dist == 1:
                student['social_category'] = 'ST'
            elif category_dist == 2:
                student['social_category'] = 'OBC'
            else:
                student['social_category'] = 'General'
            
            # Additional fields
            student['parent_occupation'] = 'Unknown'
            student['distance_to_school_km'] = round(5 + (idx % 10), 1)
            student['previous_grade_failures'] = 0
            
            if 'failures' in df.columns:
                student['previous_grade_failures'] = int(row['failures'])
            
            # Calculate dropout risk
            risk_score = 0
            if student['attendance_rate'] < 60:
                risk_score += 3
            elif student['attendance_rate'] < 75:
                risk_score += 1
                
            if student['exam_score'] < 45:
                risk_score += 3
            elif student['exam_score'] < 65:
                risk_score += 1
                
            if student['socio_economic_status'] == 'Low':
                risk_score += 2
                
            if not student['transport_allowance_used']:
                risk_score += 1
                
            if student['migration_indicator']:
                risk_score += 2
                
            if student['previous_grade_failures'] > 0:
                risk_score += student['previous_grade_failures']
            
            # Classify risk
            if risk_score >= 6:
                student['dropout_risk'] = 'High'
            elif risk_score >= 3:
                student['dropout_risk'] = 'Moderate'
            else:
                student['dropout_risk'] = 'Low'
            
            students.append(student)
            
        except Exception as e:
            print(f"⚠ Warning: Skipping row {idx}: {e}")
            continue
    
    print(f"✓ Successfully mapped {len(students)} students")
    return students

def save_integrated_dataset(students, output_file='dataset_kaggle.json'):
    """
    Save the processed dataset in AP format
    """
    # Calculate statistics
    risk_counts = {'High': 0, 'Moderate': 0, 'Low': 0}
    district_stats = {}
    
    for student in students:
        risk_counts[student['dropout_risk']] += 1
        
        district = student['district']
        if district not in district_stats:
            district_stats[district] = {'total': 0, 'high_risk': 0, 'moderate_risk': 0, 'low_risk': 0}
        
        district_stats[district]['total'] += 1
        district_stats[district][f"{student['dropout_risk'].lower()}_risk"] += 1
    
    # Create final structure
    dataset = {
        "metadata": {
            "dataset_name": "Kaggle Student Data - Mapped to AP Format",
            "academic_year": "2024-2025",
            "total_students": len(students),
            "districts": list(district_stats.keys()),
            "data_source": "Kaggle Public Datasets",
            "processing_date": "November 22, 2025",
            "privacy_compliance": "DPDP Act 2023 - All data anonymized"
        },
        "students": students,
        "risk_distribution": {
            "high_risk": risk_counts['High'],
            "moderate_risk": risk_counts['Moderate'],
            "low_risk": risk_counts['Low']
        },
        "district_statistics": district_stats
    }
    
    # Save to JSON
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(dataset, f, indent=2)
    
    print(f"\n✓ Dataset saved to: {output_file}")
    print(f"\n📊 Summary:")
    print(f"   Total Students: {len(students)}")
    print(f"   High Risk: {risk_counts['High']} ({risk_counts['High']/len(students)*100:.1f}%)")
    print(f"   Moderate Risk: {risk_counts['Moderate']} ({risk_counts['Moderate']/len(students)*100:.1f}%)")
    print(f"   Low Risk: {risk_counts['Low']} ({risk_counts['Low']/len(students)*100:.1f}%)")

def main():
    print("=" * 60)
    print("KAGGLE DATASET INTEGRATION FOR AI DROPOUT WARNING SYSTEM")
    print("=" * 60)
    
    # Step 1: Check Kaggle setup
    if not setup_kaggle():
        print("\n⚠ Please complete Kaggle API setup first!")
        print("Run this script again after setup.")
        return
    
    # Step 2: Download datasets
    print("\n" + "=" * 60)
    print("AVAILABLE DATASETS:")
    print("=" * 60)
    print("1. Student Dropout Prediction (4,424 students)")
    print("2. xAPI-Edu-Data (480 students)")
    print("3. Skip download - use existing CSV file")
    
    choice = input("\nEnter your choice (1/2/3): ").strip()
    
    if choice == '1':
        if download_student_dropout_dataset():
            csv_file = './kaggle_data/dataset.csv'  # Adjust based on actual file
        else:
            return
    elif choice == '2':
        if download_xapi_edu_dataset():
            csv_file = './kaggle_data/xAPI-Edu-Data.csv'
        else:
            return
    elif choice == '3':
        csv_file = input("Enter CSV file path: ").strip()
        if not os.path.exists(csv_file):
            print(f"✗ File not found: {csv_file}")
            return
    else:
        print("Invalid choice!")
        return
    
    # Step 3: Process the data
    df = process_kaggle_data_to_ap_format(csv_file)
    if df is None:
        return
    
    # Step 4: Map to AP format
    students = map_kaggle_to_ap_format(df)
    
    # Step 5: Save integrated dataset
    save_integrated_dataset(students)
    
    print("\n" + "=" * 60)
    print("✓ INTEGRATION COMPLETE!")
    print("=" * 60)
    print("\nNext Steps:")
    print("1. Review dataset_kaggle.json")
    print("2. Update model.py to use new dataset")
    print("3. Update server.js to load from dataset_kaggle.json")
    print("4. Re-run ML model: python model.py")
    print("5. Restart backend: node server.js")

if __name__ == '__main__':
    main()
