# Backend for delphi evaluation app 2025/5
## Deployed to https://delphi.cphbusinessapps.dk
## Available endpoints:
- `GET /api/routes/` - Returns a list of all available endpoints.
- `GET /api/answers/` - Returns a list of all answers.
- `POST /api/answers/` - Create a new answer.
- `PUT /api/answers/:answerId/` - Update an existing answer to add a comment by the teacher (at class discussion)
- `GET /api/ratings/:answerId/` - Returns a list of all ratings for a specific answer.
- `POST /api/ratings/:answerId/` - Create a new rating for a specific answer.
- `POST /api/classes/` - Creates a new Class
- `PUT /api/classes/:classId/` - Updates a Class (to change name, number of students or eval facilitator)

