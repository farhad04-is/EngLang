import React, { useState, useContext } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { VideoContext } from '../Context/VideoProvider'; // ' ' boşluğu kaldırıldı

// parseVTT fonksiyonunu import ediyoruz
import { parseVTT } from '../utils/subtitleParser'; // <-- Bu satırı ekleyin (yola dikkat edin!)

const VideoInputPage = () => {
  const [videoInputUrl, setVideoInputUrl] = useState('');
  const { setVideoData } = useContext(VideoContext);
  const navigate = useNavigate();

  const handleLoadVideo = async () => {
    if (!videoInputUrl) return alert('Video URL girin.');

    try {
      const response = await axios.get(
        `http://localhost:8082/v1/EngLang/video-subtitle?url=${encodeURIComponent(videoInputUrl)}`
      );
      const subtitleText = response.data.subtitle;

      const blob = new Blob([subtitleText], { type: 'text/vtt' });
      const blobUrl = URL.createObjectURL(blob);

      const parsed = parseVTT(subtitleText); // Şimdi parseVTT burada erişilebilir

      setVideoData({
        videoUrl: response.data.videoUrl,
        subtitleBlobUrl: blobUrl,
        parsedSubtitles: parsed,
      });

      navigate('/watch');
    } catch (err) {
      console.error(err);
      alert('Video yüklenemedi.');
    }
  };

  return (
    <div className="p-4">
      <input
        type="text"
        value={videoInputUrl}
        onChange={(e) => setVideoInputUrl(e.target.value)}
        placeholder="Video URL girin"
        className="border p-2 mb-2 w-full"
      />
      <button onClick={handleLoadVideo} className="bg-blue-600 text-white px-4 py-2 rounded">
        Videoyu Aç
      </button>
    </div>
  );
};

export default VideoInputPage;