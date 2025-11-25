# 📋 Intervention Management Guide

## Where to View Interventions

### 1. **Dashboard (Recommended)**

Open `dashboard.html` in your browser while the server is running:

**Location in Dashboard:**
- Scroll down to the "📋 Recent Interventions" section
- Shows the last 20 interventions by default
- Click "View All" to see up to 500 interventions

**Features:**
- ✅ Real-time list of all logged interventions
- ✅ Student ID, action description, and timestamp
- ✅ Who performed the intervention
- ✅ Time ago display (e.g., "5 minutes ago", "2 hours ago")
- ✅ Auto-refresh after logging new intervention
- ✅ Manual refresh with "Refresh List" button

---

### 2. **API Endpoints**

You can also access interventions programmatically via REST API:

#### **Get All Interventions**
```bash
GET http://localhost:3000/interventions

# With query parameters:
GET http://localhost:3000/interventions?limit=50
GET http://localhost:3000/interventions?studentId=ST001
```

**Response:**
```json
{
  "total": 25,
  "showing": 20,
  "interventions": [
    {
      "id": 1,
      "studentId": "ST001",
      "type": "Counseling",
      "description": "Parent meeting scheduled",
      "actionedBy": "Teacher Rao",
      "date": "2025-11-25T10:30:00.000Z",
      "timestamp": 1732532400000
    }
  ]
}
```

#### **Get Specific Intervention**
```bash
GET http://localhost:3000/interventions/:id

# Example:
GET http://localhost:3000/interventions/1
```

#### **Log New Intervention**
```bash
POST http://localhost:3000/interventions
Content-Type: application/json

{
  "studentId": "ST001",
  "type": "Counseling",
  "description": "Parent meeting scheduled to discuss attendance",
  "actionedBy": "Teacher Rao"
}
```

---

### 3. **Database (Production)**

When using PostgreSQL (via Docker or GCP):

```sql
-- Connect to database
psql -U appuser -d dropout_prevention

-- View all interventions
SELECT * FROM interventions ORDER BY intervention_date DESC LIMIT 20;

-- View interventions for specific student
SELECT * FROM interventions WHERE student_id = 'ST001';

-- View interventions by district
SELECT * FROM interventions WHERE district = 'Visakhapatnam';

-- View pending interventions
SELECT * FROM interventions WHERE status = 'pending';

-- Get intervention statistics
SELECT 
  district, 
  COUNT(*) as total_interventions,
  COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed
FROM interventions
GROUP BY district;
```

---

## How to Log Interventions

### **Option 1: Dashboard UI**

1. Start the server: `npm start`
2. Open `dashboard.html` in browser
3. Scroll to "🎯 Log Intervention" section
4. Fill in:
   - Student ID (e.g., ST001)
   - Intervention action description
   - Your name
5. Click "Record Intervention"
6. View the logged intervention in "Recent Interventions" section below

### **Option 2: Quick Action Button**

1. In the "Priority At-Risk Students" list
2. Click the "Quick Action" button next to any student
3. Form will auto-fill with student ID
4. Add your intervention details
5. Click "Record Intervention"

### **Option 3: API Call**

Using curl:
```bash
curl -X POST http://localhost:3000/interventions \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "ST001",
    "type": "Counseling",
    "description": "Scheduled counseling session for attendance improvement",
    "actionedBy": "Teacher Kumar"
  }'
```

Using JavaScript:
```javascript
async function logIntervention() {
  const response = await fetch('http://localhost:3000/interventions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      studentId: 'ST001',
      type: 'Academic Support',
      description: 'Extra tutoring arranged for mathematics',
      actionedBy: 'Teacher Devi'
    })
  });
  const data = await response.json();
  console.log(data);
}
```

---

## Intervention Data Structure

### **In-Memory Storage (Current)**

Interventions are stored in the `interventions` array in `server.js`:

```javascript
{
  id: 1,                                    // Auto-generated ID
  studentId: "ST001",                       // Student identifier
  type: "Counseling",                       // Intervention type
  description: "Parent meeting scheduled",   // Action description
  actionedBy: "Teacher Rao",                // Who performed it
  date: "2025-11-25T10:30:00.000Z",        // ISO timestamp
  timestamp: 1732532400000                  // Unix timestamp for sorting
}
```

**⚠️ Note**: In-memory storage means interventions are **lost when server restarts**. For production, use PostgreSQL database.

### **PostgreSQL Storage (Production)**

When using Docker or GCP deployment:

```sql
CREATE TABLE interventions (
    id SERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    intervention_type VARCHAR(100) NOT NULL,
    intervention_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    assigned_to VARCHAR(100),
    status VARCHAR(50) DEFAULT 'pending',
    district VARCHAR(100),
    risk_score DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Integration with LEAP App

The system has a dedicated LEAP integration endpoint for field workers:

```bash
POST http://localhost:3000/leap/field-update

{
  "studentId": "ST001",
  "attendanceStatus": "present",
  "notes": "Student attended class, appears engaged",
  "location": {
    "lat": 17.6868,
    "lng": 83.2185,
    "district": "Visakhapatnam"
  }
}
```

This allows field workers using LEAP mobile app to:
- Update student attendance in real-time
- Add field observation notes
- Tag location (GPS coordinates)
- Trigger automatic interventions for chronic absenteeism

---

## Intervention Types

Common intervention categories tracked:

- **Counseling** - Student/parent counseling sessions
- **Academic Support** - Tutoring, extra classes
- **Transport Support** - Transport allowance provision
- **Financial Aid** - Scholarships, uniform support
- **Health Support** - Medical checkups, nutrition programs
- **Parent Engagement** - Parent meetings, home visits
- **Mentorship** - One-on-one mentoring programs

---

## Filtering Interventions

### **By Student ID**
```bash
GET http://localhost:3000/interventions?studentId=ST001
```

### **Limit Results**
```bash
GET http://localhost:3000/interventions?limit=10
```

### **Combined Filters**
```bash
GET http://localhost:3000/interventions?studentId=ST001&limit=5
```

---

## Export Interventions

### **For Reports (Future Enhancement)**

Add export functionality to dashboard:
```javascript
function exportInterventions() {
  fetch('http://localhost:3000/interventions?limit=1000')
    .then(res => res.json())
    .then(data => {
      // Convert to CSV
      const csv = convertToCSV(data.interventions);
      downloadCSV(csv, 'interventions_report.csv');
    });
}
```

### **For Analytics**

Query database directly:
```sql
-- Monthly intervention trends
SELECT 
  DATE_TRUNC('month', intervention_date) as month,
  COUNT(*) as total_interventions,
  COUNT(DISTINCT student_id) as unique_students
FROM interventions
GROUP BY month
ORDER BY month DESC;

-- Most common intervention types
SELECT 
  intervention_type,
  COUNT(*) as count
FROM interventions
GROUP BY intervention_type
ORDER BY count DESC;
```

---

## Best Practices

1. **Log interventions immediately** after action is taken
2. **Be specific** in descriptions (e.g., "Scheduled parent meeting on Nov 25 at 3 PM" vs "Parent contacted")
3. **Track outcomes** by updating intervention status (pending → completed)
4. **Review regularly** to identify which interventions are most effective
5. **Use consistent student IDs** to enable proper tracking across systems

---

## Troubleshooting

### **Interventions not showing up**

1. Check server is running: `npm start`
2. Verify API endpoint: `http://localhost:3000/interventions`
3. Check browser console for errors (F12)
4. Ensure CORS is enabled in server.js

### **Interventions lost after restart**

This is expected behavior with in-memory storage. Solutions:
- Use PostgreSQL database (see `docker-compose.yml`)
- Deploy to GCP with Cloud SQL (see `GCP_DEPLOYMENT_PLAN.md`)

### **Cannot access from LEAP app**

1. Ensure server is accessible from network (not just localhost)
2. Update API_BASE URL in LEAP app configuration
3. Check firewall rules allow port 3000
4. For production, use Cloud Run URL instead of localhost

---

## Future Enhancements

- [ ] Export interventions to CSV/Excel
- [ ] Intervention effectiveness tracking (before/after risk scores)
- [ ] Automated intervention recommendations based on risk factors
- [ ] Push notifications for pending interventions
- [ ] Calendar integration for scheduled follow-ups
- [ ] Photo attachments (home visits, consent forms)
- [ ] Multi-language support (Telugu, Hindi)
- [ ] Offline mode for field workers in areas with poor connectivity

---

**📖 For more details, see:**
- [README.md](./README.md) - Project overview and setup
- [GCP_DEPLOYMENT_PLAN.md](./GCP_DEPLOYMENT_PLAN.md) - Production deployment with PostgreSQL
- [init.sql](./init.sql) - Database schema for interventions table
