from pydantic import BaseModel

class DrugResponse(BaseModel):
    matched_drug: str