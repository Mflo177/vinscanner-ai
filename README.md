# 🚗 VinScanner AI App 

VinScanner AI is an intelligent mobile application that applies **computer vision** and **machine learning** principles to detect, extract, and organize **Vehicle Identification Numbers (VINs)** in real-time.  
It combines **Google ML Kit’s on-device OCR** with a structured **data export pipeline**, reflecting an end-to-end AI workflow — from **data acquisition → processing → structured output**.

---

## 🧩 Core Features

- 🔍 **AI-Powered VIN Recognition** – Leverages ML Kit’s OCR to detect and extract VINs accurately.  
- ⚙️ **Real-Time Processing** – Camera overlay with a visual focus rectangle guides scanning precision.  
- 📁 **Dynamic Data Handling** – Automatically organizes scanned VINs into editable lists.  
- 📊 **Excel/CSV Export Pipeline** – Converts structured lists into `.xlsx` format (Apache POI), similar to `pandas.DataFrame.to_excel()` in Python.  
- 📬 **Automated Email Sharing** – Sends structured datasets to external systems or teams.  
- 💡 **Offline AI Model** – Uses on-device inference (no cloud required).  

---

## 🧰 Tech Stack

### 🧬 AI / Data Processing
- **Google ML Kit (Text Recognition API)** – OCR inference  
- **Apache POI** – Excel export pipeline (mimicking pandas workflows)

### 💻 Software Engineering
- **Android Studio (Java)** – App framework  
- **ConstraintLayout, RecyclerView, Toolbar** – UI/UX layer  
- **Gradle** – Build automation  
- **Material Design** – Modern interface  

---

