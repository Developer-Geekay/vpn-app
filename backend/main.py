from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from pydantic import BaseModel
from typing import Optional
import uvicorn

app = FastAPI(title="Geekay VPN API")

# Mock User Database
USERS = {
    "testuser": {
        "username": "testuser",
        "password": "testpassword",  # In production, use hashed passwords
        "vpn_ip": "10.0.0.2/32"
    }
}

class Token(BaseModel):
    access_token: str
    token_type: str

class User(BaseModel):
    username: str
    vpn_ip: str

class VpnConfig(BaseModel):
    interface_private_key: str
    interface_address: str
    peer_public_key: str
    peer_endpoint: str
    peer_allowed_ips: str

@app.post("/login", response_model=Token)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user = USERS.get(form_data.username)
    if not user or form_data.password != user["password"]:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    # Simple token for demonstration
    return {"access_token": f"token_{user['username']}", "token_type": "bearer"}

@app.get("/config", response_model=VpnConfig)
async def get_vpn_config(token: str):
    # In production, verify JWT token here
    username = token.replace("token_", "")
    user = USERS.get(username)
    if not user:
        raise HTTPException(status_code=401, detail="Invalid token")

    # Mock WireGuard Config
    # Replace these with your actual server details
    return VpnConfig(
        interface_private_key="REPLACE_WITH_GENERATED_PRIVATE_KEY",
        interface_address=user["vpn_ip"],
        peer_public_key="YOUR_SERVER_PUBLIC_KEY",
        peer_endpoint="YOUR_SERVER_IP:51820",
        peer_allowed_ips="0.0.0.0/0"
    )

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
