import React, { useState } from 'react';
import { Route, Routes /* BrowserRouter'ı buradan kaldırın */ } from 'react-router-dom';

// Diğer bileşenleriniz
import AllWords from './Pages/AllWords';
import Message from './Pages/Message';
import Profile from './Pages/Profile';
import SearcFriends from './Pages/SearcFriends';
import VideoLists from './Pages/VideoLists';
import NoPage from './Pages/NoPage';
import Animasya from './Animasya';
import Layout from './MainLayout/Layout';
import Playlists from './Pages/Playlists';
import VideoInputPage from './Pages/VideoInputPage';
import VideoWatchPage from './Pages/VideoWatch';

function App() {
  const [showAnimation, setShowAnimation] = useState(true);

  const handleAnimationComplete = () => {
    console.log("Animasyon tamamlandı, ana içeriğe geçiliyor!");
    setShowAnimation(false);
  };

  return (
    <div className="App">
      {showAnimation ? (
        <Animasya onAnimationComplete={handleAnimationComplete} />
      ) : (
        // BrowserRouter'ı buradan kaldırdık, çünkü o zaten main.jsx'te
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<VideoInputPage />} />
            <Route path="messages" element={<Message />} />
            <Route path="profile" element={<Profile />} />
            <Route path="searchFriends" element={<SearcFriends />} />
            <Route path="videolists" element={<VideoLists />} />
            <Route path="playlists" element={<Playlists />} />
           
            <Route path="/watch" element={<VideoWatchPage />} />
            <Route path="*" element={<NoPage />} />
          </Route>
        </Routes>
      )}
    </div>
  );
}

export default App;