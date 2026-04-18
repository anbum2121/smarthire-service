\# SmartHire — AI-Powered Resume Validator



An intelligent resume analysis system powered by Google Gemini AI that helps candidates optimize their resumes for specific job descriptions and ATS systems.



\## What it does



Paste your resume and a job description — SmartHire uses Google Gemini AI to instantly analyze the match and tell you exactly what to fix.



\## Architecture



Client → REST API → ResumeAnalysisService → Gemini AI API

&#x20;                         ↓

&#x20;                    H2 Database (stores all analysis results)



\## Tech Stack



\- Java 21 + Spring Boot 3.2.5

\- Google Gemini AI API (gemini-2.5-flash)

\- Spring Data JPA + H2 Database

\- Apache PDFBox — PDF text extraction

\- WebFlux WebClient — async HTTP calls to Gemini

\- Lombok — boilerplate reduction

\- Maven — build tool



\## Key Features



\- AI-powered resume vs job description match scoring (0-100%)

\- Missing keywords detection — shows exactly what's missing

\- Skills gap analysis — what to learn next

\- ATS compatibility rating (EXCELLENT/GOOD/AVERAGE/POOR)

\- PDF resume upload and text extraction

\- All analysis results saved to database for history tracking



\## API Endpoints



Analyze Resume (JSON)

POST http://localhost:8081/api/v1/resume/analyze



Body:

{

&#x20;   "candidateName": "Your Name",

&#x20;   "jobDescription": "paste job description here",

&#x20;   "resumeText": "paste resume text here"

}



Upload PDF Resume

POST http://localhost:8081/api/v1/resume/analyze/pdf

Form Data: file (PDF), candidateName, jobDescription



Get Analysis by Candidate

GET http://localhost:8081/api/v1/resume/candidate/{name}



Get Top Matches

GET http://localhost:8081/api/v1/resume/top-matches?minScore=70



\## Sample Response



{

&#x20;   "id": 1,

&#x20;   "candidateName": "Anbalagan M",

&#x20;   "matchScore": 98,

&#x20;   "missingKeywords": "None",

&#x20;   "skillsGap": "No gaps identified for this role",

&#x20;   "suggestions": "Strong match — highlight Kafka experience more prominently",

&#x20;   "atsCompatibility": "EXCELLENT",

&#x20;   "analyzedAt": "2026-04-18T11:11:42"

}



\## Getting Started



Prerequisites:

\- Java 21+

\- Maven 3.9+

\- Google Gemini API key (free at aistudio.google.com)



Run the Application:

mvn spring-boot:run



Add your Gemini API key in application.properties:

gemini.api.key=YOUR\_API\_KEY\_HERE



\## Author



Anbalagan M — Java Backend Developer

GitHub: https://github.com/anbum2121

