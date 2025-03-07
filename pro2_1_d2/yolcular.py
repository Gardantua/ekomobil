from abc import ABC, abstractmethod

class Yolcu(ABC):
    def __init__(self, isim, yas,  nakit_bakiye, kredi_karti_bakiye, kentkart_bakiye):
        self.isim = isim
        self.yas = yas
        self.nakit_bakiye = nakit_bakiye
        self.kredi_karti_bakiye = kredi_karti_bakiye
        self.kentkart_bakiye = kentkart_bakiye

    @abstractmethod
    def indirim_orani(self):
        pass
"""
    def __str__(self):
        return f"{self.isim}, {self.yas} yaşında"

    def odeme_yap(self, tutar):
        if self.nakit_bakiye >= tutar:
            self.nakit_bakiye -= tutar
            return True
        elif self.kredi_karti_bakiye >= tutar:
            self.kredi_karti_bakiye -= tutar
            return True
        elif self.kentkart_bakiye >= tutar:
            self.kentkart_bakiye -= tutar
            return True
        else:
            return False
"""
class Ogrenci(Yolcu):
    def indirim_orani(self):
        return 0.5  # Öğrenciler için %50 indirim

class GenelYolcu(Yolcu):
    def indirim_orani(self):
        return 0  # Genel yolcular için indirim yok
    
class Yasli(Yolcu):
    def indirim_orani(self):
        return 0.35  # Yaşlılar için %35 indirim