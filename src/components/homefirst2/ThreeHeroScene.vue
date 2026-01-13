<template>
  <div ref="container" class="three-scene-container"></div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

const container = ref(null);
let scene, camera, renderer, controls;
let spheres = [];
let torus;
let stars;
let animationId;

onMounted(() => {
  initScene();
  animate();
  window.addEventListener('resize', onWindowResize);
});

onBeforeUnmount(() => {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  window.removeEventListener('resize', onWindowResize);
  if (renderer) {
    renderer.dispose();
  }
});

function initScene() {
  if (!container.value) return;

  // Scene setup
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x000000);

  // Camera
  camera = new THREE.PerspectiveCamera(45, container.value.clientWidth / container.value.clientHeight, 0.1, 1000);
  camera.position.set(0, 0, 10);

  // Renderer
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  container.value.appendChild(renderer.domElement);

  // Controls
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.screenSpacePanning = false;
  controls.maxPolarAngle = Math.PI / 2;
  controls.enableZoom = false;

  // Lighting
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
  scene.add(ambientLight);

  const pointLight = new THREE.PointLight(0xffffff, 1);
  pointLight.position.set(10, 10, 10);
  scene.add(pointLight);

  // Objects
  createSpheres();
  createTorus();
  createStars();
}

function createSpheres() {
  const geometry = new THREE.SphereGeometry(1, 32, 32);
  const colors = [0x4F46E5, 0x9333EA, 0xC5A059];

  for (let i = 0; i < 3; i++) {
    const material = new THREE.MeshStandardMaterial({
      color: colors[i],
      roughness: 0.1,
      metalness: 0.1,
      clearcoat: 1.0,
      clearcoatRoughness: 0.1,
    });
    const sphere = new THREE.Mesh(geometry, material);
    sphere.position.set(
      (Math.random() - 0.5) * 6,
      (Math.random() - 0.5) * 6,
      (Math.random() - 0.5) * 6
    );
    scene.add(sphere);
    spheres.push(sphere);
  }
}

function createTorus() {
  const geometry = new THREE.TorusGeometry(3, 0.1, 16, 100);
  const material = new THREE.MeshStandardMaterial({
    color: 0xC5A059,
    roughness: 0.1,
    metalness: 0.1,
    clearcoat: 1.0,
  });
  torus = new THREE.Mesh(geometry, material);
  torus.rotation.x = Math.PI / 2;
  scene.add(torus);
}

function createStars() {
  const geometry = new THREE.BufferGeometry();
  const count = 1000;
  const positions = new Float32Array(count * 3);

  for (let i = 0; i < count; i++) {
    positions[i * 3] = (Math.random() - 0.5) * 100;
    positions[i * 3 + 1] = (Math.random() - 0.5) * 100;
    positions[i * 3 + 2] = (Math.random() - 0.5) * 100;
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  const material = new THREE.PointsMaterial({
    color: 0xffffff,
    size: 0.1,
  });
  stars = new THREE.Points(geometry, material);
  scene.add(stars);
}

function animate() {
  animationId = requestAnimationFrame(animate);

  // Animate spheres
  spheres.forEach((sphere, i) => {
    sphere.position.y += Math.sin(Date.now() * 0.001 + i) * 0.005;
    sphere.rotation.x += 0.01;
    sphere.rotation.z += 0.01;
  });

  // Animate torus
  if (torus) {
    torus.rotation.y += 0.002;
    torus.rotation.x += 0.001;
  }

  // Animate stars
  if (stars) {
    stars.rotation.z += 0.0005;
  }

  if (controls) controls.update();
  if (renderer && scene && camera) renderer.render(scene, camera);
}

function onWindowResize() {
  if (!container.value || !camera || !renderer) return;

  camera.aspect = container.value.clientWidth / container.value.clientHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
}
</script>

<style scoped>
.three-scene-container {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 0;
  overflow: hidden;
}
</style>
