from yolcular import Ogrenci, GenelYolcu, Yasli
from duraklar import Durak
from data import load_data, create_all_graphs, draw_graph
from rota_hesaplayici import rota_hesapla

def birlestir_int(sayi1, sayi2):
    return int(str(sayi1) + str(sayi2))

ucretsiz_gunler = [
    11,   # 1 Ocak - Yılbaşı
    234,  # 23 Nisan - Ulusal Egemenlik ve Çocuk Bayramı
    15,   # 1 Mayıs - Emek ve Dayanışma Günü
    195,  # 19 Mayıs - Atatürk'ü Anma, Gençlik ve Spor Bayramı
    157,  # 15 Temmuz - Demokrasi ve Milli Birlik Günü
    308,  # 30 Ağustos - Zafer Bayramı
    2910,  # 29 Ekim - Cumhuriyet Bayramı
]
print(ucretsiz_gunler)

# Tarihleri sıralı hale getirme
ucretsiz_gunler.sort()

def binary_search(arr, target):
    global topluTasimaUcretsizseBir
    low, high = 0, len(arr) - 1

    while low <= high:
        mid = (low + high) // 2
        if arr[mid] == target:
            topluTasimaUcretsizseBir = 1
            return f"Bugün toplu taşıma ücretsiz."
        elif arr[mid] < target:
            low = mid + 1
        else:
            high = mid - 1

def main():
    isim = input("Yolcunun ismini girin: ")
    yas = int(input("Yolcunun yaşını girin: "))
    nakit_bakiye = float(input("Nakit bakiyeyi girin: "))
    kredi_karti_bakiye = float(input("Kredi kartı bakiyesini girin: "))
    kentkart_bakiye = float(input("Kentkart bakiyesini girin: "))
    gun = input("Kaçıncı ay olduğunu girin: ")
    ay = input("Kaçıncı ay olduğunu girin: ")
    secim = int(input("Konumunuzu durak olarak giricekseniz 1, enlem ve boylam olarak giricekseniz 2'ye basınız: "))
    if secim == 1:
        konum_durak = input("Durak adını girin: ")
    else:
        konum_enlem = input("Yolcunun enlemini girin: ")
        konum_boylam = input("Yolcunun boylamını girin: ")
    secimm = int(input("Hedef konumu durak olarak giricekseniz 1, enlem ve boylam olarak giricekseniz 2'ye basınız: "))
    if secimm == 1:
        hedef_durak = input("Durak adını girin: ")
    else:
        hedef_enlem = input("Hedef konumun enlemini girin: ")
        hedef_boylam = input("Hedef konumun boylamını girin: ")

    tarih = birlestir_int(gun, ay)
    binary_search(ucretsiz_gunler, tarih)

    if yas < 25:
        yolcu = Ogrenci(isim, yas, nakit_bakiye, kredi_karti_bakiye, kentkart_bakiye)
    elif yas > 65:
        yolcu = Yasli(isim, yas, nakit_bakiye, kredi_karti_bakiye, kentkart_bakiye )
    else:
        yolcu = GenelYolcu(isim, yas, nakit_bakiye, kredi_karti_bakiye, kentkart_bakiye)

    # Taksi ücretlerini alma
    data = load_data('/home/yunus/Masaüstü/pro2_1_d2/veriseti.json')
    taxi_opening_fee = data['taxi']['openingFee']
    taxi_cost_per_km = data['taxi']['costPerKm']
    print(f"Taksi açılış ücreti: {taxi_opening_fee} TL")
    print(f"Taksi km başına ücret: {taxi_cost_per_km} TL")

    # Grafları oluştur
    all_stops_graph, bus_stops_graph, tram_stops_graph = create_all_graphs('/home/yunus/Masaüstü/pro2_1_d2/veriseti.json')

    # Tüm duraklar için yönlü grafı çiz
    draw_graph(all_stops_graph)

    # Sadece otobüs durakları için yönlü grafı çiz
    draw_graph(bus_stops_graph)

    # Sadece tramvay durakları için yönlü grafı çiz
    draw_graph(tram_stops_graph)

    # Rota hesapla
    baslangic_durak = konum_durak if secim == 1 else None
    hedef_durak = hedef_durak if secimm == 1 else None
    rota = rota_hesapla(all_stops_graph, baslangic_durak, hedef_durak)
    print(f"Rota: {rota}")

    # Grafı çiz ve rotayı vurgula
    draw_graph(all_stops_graph, path=rota)

if __name__ == "__main__":
    main()