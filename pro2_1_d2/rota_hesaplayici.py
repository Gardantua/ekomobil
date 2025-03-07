import networkx as nx
from data import create_all_graphs, draw_graph

def en_kisa_yol_hesapla(graph, baslangic, hedef):
    try:
        # En kısa yolu hesapla
        shortest_path = nx.shortest_path(graph, source=baslangic, target=hedef, weight='mesafe')
        return shortest_path
    except nx.NetworkXNoPath:
        return None

def rota_hesapla(graph, baslangic, hedef):
    rota = en_kisa_yol_hesapla(graph, baslangic, hedef)
    if rota:
        print(f"En kısa rota: {rota}")
        return rota
    else:
        print("Belirtilen duraklar arasında bir yol bulunamadı.")
        return []

# Örnek kullanım
if __name__ == "__main__":
    # Verileri yükle ve grafları oluştur
    all_stops_graph, bus_stops_graph, tram_stops_graph = create_all_graphs('/home/yunus/Masaüstü/pro2_1_d2/veriseti.json')

    # Başlangıç ve hedef durakları belirle
    baslangic_durak = 'bus_otogar'
    hedef_durak = 'bus_symbolavm'

    # Rota hesapla
    rota = rota_hesapla(all_stops_graph, baslangic_durak, hedef_durak)
    print(f"Rota: {rota}")

    # Grafı çiz ve rotayı vurgula
    draw_graph(all_stops_graph, path=rota)