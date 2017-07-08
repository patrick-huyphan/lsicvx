package pt.DocTermBuilder;

import java.util.ArrayList;
import java.util.List;

public class StopWordList
{
	List<String> stopWord = new ArrayList<String>();
//	List<String> stopWordV = new ArrayList<String>();
	public StopWordList(String fileName)
	{}
	
	StopWordList()
	{
		String [] stopW= {"a","about","above","across","after","afterwards","again","against","all","almost","alone","along","already","also","although","always","am",
				"among","amongst","amoungst","amount","an","and","another","any","anyhow","anyone","anything","anyway","anywhere","are","aren’t","around","as","at","back",
				"be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","between","beyond","bill",
				"both","bottom","but","by","call","can","cannot","cant","can’t","co","computer","con","could","couldnt","couldn’t","cry","de","describe","detail","did",
				"didn’t","do","does","doesn’t","doing","done","don’t","down","due","during","each","eg","eight","either","eleven","else","elsewhere","empty","enough",
				"etc","even","ever","every","everyone","everything","everywhere","except","few","fifteen","fify","fill","find","fire","first","five","for","former",
				"formerly","forty","found","four","from","front","full","further","get","give","go","had","hadn’t","has","hasnt","hasn’t","have","haven’t","having",
				"he","he’d","he’ll","hence","her","here","hereafter","hereby","herein","here’s","hereupon","hers","herse�?","herself","he’s","him","himse�?","himself",
				"his","how","however","how’s","hundred","i","i’d","ie","if","i’ll","i’m","in","inc","indeed","interest","into","is","isn’t","it","its","it’s","itse\\�?",
				"itself","i’ve","keep","last","latter","latterly","least","less","let’s","ltd","made","many","may","me","meanwhile","might","mill","mine","more","moreover",
				"most","mostly","move","much","must","mustn’t","my","myse\\�?","myself","name","namely","neither","never","nevertheless","next","nine","no","nobody",
				"none","noone","nor","not","nothing","now","nowhere","of","off","often","on","once","one","only","onto","or","other","others","otherwise","ought",
				"our","ours","ourselves","out","over","own","part","per","perhaps","please","put","rather","re","same","see","seem","seemed","seeming","seems","serious",
				"several","shan’t","she","she’d","she’ll","she’s","should","shouldn’t","show","side","since","sincere","six","sixty","so","some","somehow","someone","something",
				"sometime","sometimes","somewhere","still","such","system","take","ten","than","that","that’s","the","their","theirs","them","themselves","then",
				"thence","there","thereafter","thereby","therefore","therein","there’s","thereupon","these","they","they’d","they’ll","they’re","they’ve","thick",
				"thin","third","this","those","though","three","","through","throughout","thru","thus","to","together","too","top","toward","towards","twelve","twenty",
				"two","un","under","until","up","upon","us","very","via","was","wasn’t","we","we’d","well","we’ll","were","we’re","weren’t","we’ve","what","whatever",
				"what’s","when","whence","whenever","when’s","where","whereafter","whereas","whereby","wherein","where’s","whereupon","wherever","whether","which","while",
				"whither","who","whoever","whole","whom","who’s","whose","why","why’s","will","with","within","without","won’t","would","wouldn’t","yet","you","you’d",
				"you’ll","your","you’re","yours","yourself","yourselves","you’ve"};
		
    	

		for (String string : stopW) {
			stopWord.add(string);
		}

	}
	public boolean stopWordV(String term)
	{
		String [] stopV= {"bitly","goog","itviec","www","vì","ví","http","amznto","kết_quả","muốn","mà_còn", "mình","máy","một","nguyễn", "nhanh","như","onfbme",
				"phan_", "quận_","readbi","slide","facebook", "the_", "think","thing","thì","thật", "thế", "thị_","trở","youtube","ô_kê","and", "anh_chị",
				"anh_em","anh_ta","anh_ấy","app","attend","ba_mẹ","ban_đầu","banh","bao_","be","best","beta","bill","boe","bubu","bà_con","bài_","bây_gi�?","bình",
				"bùi_","buổi_","bước", "bạn_","bất_","bộ_","caf", "cancel","candy_","care","chia","chiếc","chiếm","cho_","chototvn","chris","chung","chuyến","chuyển",
				"chào_","chuẩn","chàng","chúc","chúng","chút","chẳng","chị_em","chợ_tốt","click","code","cuối","cuốn","câu_","có_","cực_","do-","do_đó","dòng",
				"dù_","dĩ_","easy","friend","good","great","guide","happy","hay_","how_","hub","hình","hôm_", "hơn_","h�?c_","h�?i_","index","kha_khá", "khoe",
				"khác","không","kích_","link","luôn","làm_","lê_","lớn_","mini","mà_lại", "mê_mẩn","mượt_mà","mặc_","mới_","mở_","nam_","new","ngay","ngoài_",
				"nguyên_","ngày_","ngô_","ngư�?i","ngậm_ngùi","nhất","nhằm","ít_việc","vui_","võ_","vũ_","áo_thun","why_","anthony_","blogg","chính_","chủ_",
				"compan","con_","cuộc_", "cũng_nên","danh_","diễn_","found","hiện_","hàng_","hết_","hồ_","mệt_m�?i","nhận_","oakcargo","quan_","quy_","quà_",
				"rành_rẽ","ráo_riết","rõ_ràng","răm_rắp","sau_này","chi_chít","chép_miệng","chăm_chăm","cốc_cốc","cồng_k�?nh","dần_dần","dồi_dào","gi�?_đây","gập_gh�?nh",
				"g�?n_","hiển_nhiên","hoang_mang","hùng_hậu","hậu_hĩnh","h�?_cần_gì_ở_bạn","hữu_","khang_trang","kĩ_càng","lăn_tăn","lạc_lõng","lần_lượt","lập_tức",
				"miệt_mài","mãi_mãi","ngon_lành","ngu_ngốc","ngán_ngẩm","ngây_ngô","nho_nh�?","nhuần_nhuyễn","nhàm_chán","nhân_dịp","những_ai","niraj_bhawnani","nòng_cốt",
				"nếu_","phần_lớn","phần_nào","quá_tr�?i","rải_rác","sebastien_auligny","scott_hanselman","steve_job","sáng_mai","sáng_sáng",
				"sớm_muộn","thanh_huỳnh","thanh_phan","thanh_xuân","thiếu_gì","thomas_","thoải_mái","thuần_thục","thùy_trúc","thần_kỳ","thầy_","th�?i_đại","thứ_","timo_krokowski",
				"tinh_tư�?ng","tran_minh_trung","trau_","trôi_chảy","trăm_","trơn_tru","trước_","tù_túng","tại_","tất_","tối_","văn_huỳnh_duy","vặn_vẹo","vừa_","với_","what_","xinh_","yuko_adachi","úm_ba_la",
				"đôi_khi","được_cái","đ�?u_đặn","đồng_th�?i","welcom","will","vô_cùng","vĩnh_cửu","tết_","trổ_tài","tròn_trĩnh","trân_tr�?ng","tràn_đầy","biết_đi�?u","bên_cạnh","bằng_được",
				"choáng_ngợp","chí_minh","chót_vót","chần_chừ","chập_chững","chắc_chắn","cio_dialog","cung_mua","cô_đ�?ng","công_phu","cùng_với","cơ_hội_gửi_tặng_áo","donald_knuth","duy_khoa","dạt_dào",
				"einstein","giang_anh","giây_phút","giúp_","hai_bà_trưng","hoàn_hảo","hoàn_toàn","hào_nhoáng",
				"james_gosl","joe_lonsdale","joel_spolsky","john","jonathan_","kaihogyo","kevin_slavin","phil_trần","philips_hue","phương_bùi",
				"kara_swish","khoa-dev","khả_chương", "kim_loan", "leo_burnett", "malcolm_gladwell","martin_fowl","lương_thành_trung",
				"khinh_khỉnh","khẩn_cấp","kiên_","laszlo_bock","liên_tục","long_lanh","lúc_nào",
				"lại_được_gói_mang_v�?","mai_valentine","mecorp_vietnam","media_max_japan","mike_trần","miyatsu_vienam","miễn_là",
				"month","muôn_ngàn","mày_râu","mư�?i_hai","mỗi_dev","neumann","nghĩa_là","ngày","ngân_sâu","ngất_ngưởng","nhật_bản","nha_trang","north_america",
				"năm_tuổi","onftcompwtild","việt_nam","philippine","phân_vân","phú_nhuận","phải_biết","punch_entertain","pyramid_consulting","quý_giá","quốc_gia",
				"ramkumar","rakut","rangstrup","read","roomorama","san_fransisco","savvycom","asia","shinichi","singapore","sinh_nhật","siêu_phàm","so_sánh","stanford",
				"sung_sướng","sunrise_software_solution","sutrix_media","sutunam","svymksnrvje","solutions_ltd","súc_tích","sắc_bén","sắc_màu","sẵn_sàng","sặc_mùi","số_",
				"sống_","t-shirt","tai_hại","targetmedia","tech_talk","saigon","hà_nội","telerik","tham_","thao_","thiên_","thiết_thực","thiết_yếu","thiệt_thòi","thung_lũng",
				"thái_cực","thân_","thích_","thói_","thư�?ng_","thượng_hải","thần_thánh","thậm_chí","thắc_mắc","thể_loại","thể_hiện","th�?a_mãn","thống_kê","th�?i_điểm",
				"thụy_điển","thực_sự","tic_tac_toe","tiniplanet","tinymce","tiên_tiến","tiếc_nuối","tiến_hành","tiến_trình","tiếng_nói","tiếp_theo","tiếp_tục","tomo_huỳnh","toolkit",
				"toàn_bộ","toàn_diện","toàn_thư","tra_cứu","trang_chủ","tranh_cãi","trong_suốt","truy_","tràn_đầy","trái_tim","trình_tự","tròn_trĩnh","trương_","trả_giá","trả_l�?i","trần_","trổ_tài",
				"tuy_nhiên","tuyên_chiến","tuyệt_đối","twentyvn","tâm_","tây_","tìm_bug_của","tôi_tìm_","tập_hợp","tận_dụng","unit_corp","very_important_person","volkswag","vào_vai","vô_cùng",
				"vĩnh_cửu","vượt_trội","vậy_","vivaldi","đào_minh_khánh","áo_sẽ_được_gửi_cho","ikorn_solution", "framgia","sequent_asia","sonny_vũ","bluecom_solu","gameloft","harvey_nash","hella_",
				"đống_đa","đồng_đạo","đầu_lòng","đầu_tiên","đầu_tuần","đại_trần","đại_việt","được_việc","đơn_thuần","đông_đảo","đông_nam","đôi_lúc","đón_","đình_đám","đáng_","đàn_","đà_nẵng","đhqg_tphcm",
				"your_skill","wysiwyg","writ","warbler_digital","việc_gì","version","vega_corp","từ_dash","alt_plu","angry_bird","vietnam","aswig_solution","atlassian","ba_đình","bangkok","sài_gòn","bigbang",
				"biến_mất","bob_dylan","bottleneck","brainfuck","bug_bạn","buộc_phải_có","bách_khoa","b�?n_vững","bốc_lửa","bối_rối","bịt_mắt","bỗng_dưng","chân_thực","chóng_mặt","chú_thích","chỉ_định","cover",
				"craig_buckl","cách_viết","cáu_gắt","căn_bản","cảm_kích","cảm_tình","cầu_giấy","cầu_thang","cứng_","darwin_","david_","nghĩ_sao","dev_","dries_buytaert","dynam","dài_","dấn_thân","dễ_thương","east_agile",
				"eepurlcomexulh","elisoft","emobi_game","epsilon_mobile","evolution_software","fsoft_hcm","geniee_vietnam","ghi_nhận","deadline","ứng_tuyển","love_working_here","ph�?ng_vấn","giảm_giá","harvard","atlassian",
				"joncker","infoda","cinnamon","thái_lan","đột_xuất","đối_với","charle","awward","agency_revolu","close","viễn_cảnh","nexcel_solution","độc_quy�?n","hongkiat","loca","tourna","gần_gũi","đội_ngũ","độc_lập","đồng_nghĩa",
				"đỉnh_cao","đ�?n_đáp","đến_nơi","đặt_cược","đầu_hàng","đăng_kí","đoàn_kết","xác_thực","xuất_phát","xiêu_lòng","xem_xét","worksmedia","ventura","tỉnh_táo","tạo_single","tư�?ng_tận","tóm_tắt","tùy_thuộc","táo_dev",
				"twitt","tr�?ng_tâm","trào_lưu","trustcircle","troubleshoot","tourna","topology","tiêu_cực","thịnh_hành","theo_đuổi","sâu_rộng","solis_lab","phép_thuật","phá_sản","ngoại_lệ","nghĩa_đ","netflix_việt",
				"misfit_wearable","lý_giải","logigear","lint","license","lemieux","kế_tiếp","kéo_dài","kèm_cặp","kerofrog","joom_solu","hậu_quả","hàn_quốc","hongkiat","heartbled","dấu_hiệu","cảm_ứng","chiến_dịch","băn_khoăn",
				"azstack","ưa_chuộng","đồng_hành","đối_chiếu","định_vị","định_hướng","đ�?_xuất","đặc_trưng","đàm_phán","điện_từ","đi�?u_chỉnh","ít_viêc","xung_quanh","worldwide","workshop","web_service","wearable","vật_chất",
				"văn_bản","usable_security","user_interface","upgrade","unparallel","universal","tội_phạm","tổng_thể","tưởng_tượng","tưởng_chừng","tươi_sáng","tăng_tốc","tài_nguyên","tuổi_th�?","truy�?n_","toàn_quy�?n","tiết_lộ","tiêu_dùng",
				"thử_nghiệm","thắng_cuộc","thất_sủng","thảm_h�?a","thư_giãn","thông_thư�?ng","thông_thoáng","thái_độ","thách_thức","thành_phố","talent","săn_đón","sôi_động","sublime_text","stylesheet","studio","statement","spann","snapchat",
				"situa","sinh_động","sinh_tồn","shopify","server","septeni","rào_cản","reward","release","request","queri","pycogroup","phòng_chống","phát_hiện","package","orient","oren_eini","optimiza",
				"nản_chí","ý_nghĩa","brows","bitcoin","bàn_tay","bàn_đạp","bản_năng","cheetyr","constant","dao_động","dedica","district","ganbatte","gian_nan","glassegg","grapecity","horus_team","hoan_nghênh","hành_vi",
				"hôn_nhân","hạn_hẹp","hạn_chế","khoa_pham","khổng_lồ","laravel","liên_hệ","lặp_đi_lặp_lại","phụ_thuộc","hervé_vu","awesome","ăn_chơi","waterfall","vững_mạnh","xuất_thân","vaca","tạp_chí","tư_","ti�?n_bạc","thuyết_phục",
				"strucure","stacksocial","stackoverflow","techcom_securiti","template","chi_tiết","hiệp_sĩ","bioloid","thạc_sĩ","thảo_luận","tin_vui","đến_đi�?u","đánh_dấu","đi�?u_phối","đau_đầu","tổng_kết","tương_đồng","tiên_phong",
				"sáng_lập","snowball","sinh_ra","reactj","quảng_bá","pursuant","provider","protocol","prime_circa","nhắn_tin","nhân_vật","nuôi_dưỡng","nói_chuyện","ngắn_g�?n","ngạc_nhiên","ngưỡng_mộ","ngân_sách",
				"nguy_cơ","nghèo_nàn","nghiêm_tr�?ng","naiscorp","mấu_chốt","miêu_tả","memcache","malaysia","lắc_đầu","lùng_sục","lâu_đ�?i","load","linh_hồn","jobseek","james_","indonesia","hối_hận","hệ_đi�?u_hành",
				"host","hiệu_chỉnh","heartbled","head_quart","giải_thích","giao_thức","giai_đoạn","gateway","forma","feedback","firefox","dễ_dàng","discount","cảm_","đồng_phục","địa_điểm","đắc_nhân_tâm","định_nghĩa","đối_thủ","để_dành",
				"đúng_đắn","xương_máu","vinasource","tủ_lạnh","từ_chối","tập_luyện","channel","chi_phối","chu_trình","chú_tr�?ng","compil","công_bố","công_nhận","cải_tiến","kéo_theo","letterink","liệt_kê","kết_thúc",
				"đi�?u_tra","đi�?u_khiển","tấn_công","tương_xứng","tương_thích","tình_yêu","tác_phẩm","trực_tuyến","trang_phục","thanh_toán","sức_mạnh","sáng_giá","social_intranet","segmenta","problem","phương_",
				"phù_hợp","pháp_luật","phiên_bản","pay","outlier","oceannet","nội_công","ni�?m_vui","ngần_ngại","mistake","lâu_dài","lifetime","kết_thúc","kết_hợp","khả_dụng","japanese","introduc","hân_hoan","decision",
//				"optim", "độc_đáo","ưu_tiên","đ�?_nghị","đơn_giản","đóng_góp","đánh_giá","đo_lư�?ng","xứng_đáng","xuất_sắc","vận_hành","viếng_thăm","universit","tụt_hậu","tồn_tại","tích_cực","tài_trợ","trư�?ng_hợp","trình_duyệt","trang_web","ti�?m_năng",
//				"tinh_thần","tin_tưởng","thúc_đẩy","thu_hút","symfony","sitepoint","quảng_cáo","private","portab","offer","nhẹ_gánh","module","lưu_tâm","khởi_động","khôn_ngoan","hoành_tráng","ham_thích","cảnh_giới","cao_ngất","bắt_mắt","an_phận"
				};
		for(String w: stopV)
			if(term.contains(w))
				return true;
		return false;
	}
	public String replace(String term)
	{
		String [] terms={"active",
//				"activit","analy","angular", "architect", "audience","automa","awesome", "backend","balanc","bridge","certifica",
//				"chỉnh_sửa","configura","country_manag","công_ty","database","deliver","depend","develop","differen","commerce","elisoft","experience","featur","financ",
//				"firefox","follow","freel","front","frontend","fullstack","graphic","hardware","htmlcss","imple","java_","know",
//				"lợi_ích","machine_learn","magento","maint","manag","method","nặng_","objectiv","onsite","operat","opportunit","payment","pattern",
//				"platform","practic","product","program","project_mana","require","responsibilit","rollout",
//				"ruby",	//"ruby_dev","ruby_on_rail",
//				"php",
//				"machine",
//				"mobile_",//"mobile_dev","mobile_project_manag",
//				
//				"rộng_","sai_","quản_l",
//				"senior_develop","senior_java","senior_software","senior_web",
//				"startup","technical_","technolog","test","thương_mại","th�?i_gian","tiếng_anh","train","tuyển_","design",
//				"tự_động","universit","visual_studio","web_develop","window","wordpress","đơn_giản","trí_tuệ","agile_","browser","build","bắt_đầu","coursera",
//				"engine","essential","exten","includ","linus_","lập_trình","marketing_","respons","runn","tổng_giám","contribut","customer_","environ","preview","tổng_hợp","thuận_",
//				"framework","librar","tech_","solu","software","smart","script","robo","html","certi","front",
				"japanese"
				
				};
		
		for(String w: terms)
		{
			if( term.contains(w))
				return w;
		}
		return term;
		
	}
	
	public String sameMeaning(String term)
	{
		String [][] terms={{"ruby", "ruby_onrails",""},
				{"bank, banker,ngân_hàng"},
				{"java"},
				{"php"},
				{"giao_thức"},
				{"giao_dịch"}
				};
		
		for(String[] w: terms)
		{
			for(String s : w)
			if( term.contains(s))
				return w[w.length-1];
		}
		return term;
		
	}
}
