

from abc import ABC, abstractmethod

class Arac(ABC):
    def __init__(self, tip):
        self.turu = tip
        
    @abstractmethod
    def ucret_hesapla(self, mesafe):
        pass

    def __str__(self):
        return f"Araç Türü: {self.turu}"
    

class Otobus(Arac):
    def ucret_hesapla(self, mesafe):
        return mesafe * 2  # Otobüs için km başına 2 TL
    

class Tramvay(Arac):
    def ucret_hesapla(self, mesafe):
        return mesafe * 1.5  # Tramvay için km başına 1.5 TL


class Taksi(Arac):
    def __init__(self, plaka, acilis_ucreti, km_ucreti):
        super().__init__(plaka)
        self.acilis_ucreti = acilis_ucreti
        self.km_ucreti = km_ucreti

    def ucret_hesapla(self, mesafe):
        return self.acilis_ucreti + (mesafe * self.km_ucreti)
    





